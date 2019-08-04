package sharp4j.util.domain;


import java.beans.PropertyDescriptor;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 簡易ORマッパー.
 * 
 * <p>
 * <b>■概要：</b><br>
 * SpringBootの標準的なJDBC接続によるSQL結果セット（レコード形式：{@code Map<String,Object>}）から、<br>
 * エンティティクラスのインスタンスに対してデータバインドを行う、単方向の簡易的なORMapperです。<br>
 * </p>
 * <p>
 * <b>■制約：</b><br>
 * この簡易ORMを使用するには、以下の条件を満たす必要があります。
 * <ul>
 * <li>エンティティクラスのフィールドに {@link EntityDataLoader.Column} アノテーションを付与する。
 * <li>SQL結果セットのカラム名をアノテーションパラメータで指定する。<b>（フィールド名と完全一致する場合は省略可能）</b>
 * <li>アノテートしたフィールドに {@link PropertyDescriptor} でアクセス出来る事。<b>（{@code public} な {@code getter/setter} が実装されていること）</b>
 * </ul>
 * </p>
 * 
 * 
 * @author sugaryo
 *
 * @param <T> エンティティクラスの型
 * 
 * @see org.springframework.jdbc.core.JdbcTemplate
 */
public class EntityDataLoader<T> {
	
	/**
	 * エンティティのデータバインドアノテーション.
	 * 
	 * <p>
	 * <b>■：</b><br>
	 * エンティティに {@link EntityDataLoader} 部品でデータバインドしたいフィールドに付与するアノテーション。<br>
	 * SQL結果セットからバインドしたいカラム名を指定（省略した場合は、フィールド名と一致するカラムを使用）します。
	 * </p>
	 *
	 * @see java.lang.annotation.ElementType.FIELD
	 * @see java.lang.annotation.RetentionPolicy.RUNTIME
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Column {
		String value() default "";
	}
	
	/** エンティティクラスに対するORMスキーマ情報 */
	private Map<Field, Column> schema;
	
	/**
	 * コンストラクタ.
	 * 
	 * <p>
	 * 指定したエンティティクラスの型情報を解析し、<br>
	 * {@link EntityDataLoader.Column} アノテーションをスキャンして、ORマッピング情報を初期化します。
	 * </p>
	 * 
	 * @param type エンティティクラス
	 */
	public EntityDataLoader( Class<?> type ) {
		
		// 指定したエンティティフィールドのうち @Column が設定されているものを拾ってMapに詰め込む。
		this.schema = new HashMap<>();
		
		Field[] fields = type.getDeclaredFields();
		for ( Field field : fields ) {
			Column column = field.getAnnotation( Column.class );
			if ( null == column ) continue;
			
			this.schema.put( field, column );
		}
	}
	
	/**
	 * データバインド.
	 * 
	 * @param entity データバインドするエンティティインスタンス
	 * @param data   データバインドするSQL結果セット（１レコードぶん）
	 * @return SQL結果セット {@code data} から、<br>
	 *         {@link EntityDataLoader.Column} でアノテートしたデータをバインドした <br>
	 *         エンティティ {@code entity} のインスタンス参照 をそのまま返します。<br>
	 *         なお、どちらのパラメータにも {@code null} を指定できません。
	 */
	public T load( T entity, Map<String, Object> data ) {
		
		// ※ T entity インスタンスについて
		// Javaのリフレクションでは new制約 を持たせる事が出来ず、
		// 基底クラス条件を付けてもコンストラクタ制約が使えず、デフォコンに頼る事になる為、
		// 内部でインスタンス生成は行わずに、ユーティリティ利用側でインスタンス生成させている。
		// （class.newInstanseは使用しない）
		
		try {
			// ※現実的には Map じゃなくて List<Pair> みたいなもの
			for ( Entry<Field, Column> entry : this.schema.entrySet() ) {
				
				Field field = entry.getKey();
				Column column = entry.getValue();
				
				
				// SELECT結果セットのカラム名を設定
				String name = column.value().isEmpty()
						? field.getName()
						: column.value();
				
				// SELECT結果セットに含まれていない場合はスキップ。
				if ( !data.containsKey( name ) ) continue;
				
				// プロパティ経由でデータをバインド。
				Object obj = data.get( name );
				PropertyDescriptor prop = new PropertyDescriptor( field.getName(), entity.getClass() );
				prop.getWriteMethod().invoke( entity, obj );
			}
			
			return entity;
		}
		// 検査例外はランタイム例外で再スロー
		catch ( Exception ex ) {
			throw new RuntimeException( ex );
		}
	}
}

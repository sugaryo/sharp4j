package sharp4j.util.json;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * JSONマッパー.
 * 
 * <p>
 * <b>■概要：</b><br>
 * {@code jackson} の {@link ObjectMapper} インスタンスをシングルトンホルダでキャッシュし、<br>
 * 呼び出し側のコードがシンプルになるようラップし、いくつかのユーティリティを提供します。
 * </p>
 * 
 * @author sugaryo
 * 
 * @see com.fasterxml.jackson.databind.ObjectMapper
 */
public class JsonMapper {
	
	// 基本形（jacksonのObjectMapperをラップしただけのもの）
	
	/**
	 * @param json JSON文字列
	 * @param type 型情報
	 * @return JSON文字列をパースして、指定した型のオブジェクトインスタンスを返します。<br>
	 *         パースに失敗する場合は、ランタイム例外をスローします。
	 */
	public static <T> T parse( String json, Class<?> type ) {
		try {
			@SuppressWarnings("unchecked")
			T obj = (T)SingletonHolder.mapper.readValue( json, type );
			return obj;
		} catch ( Exception ex ) {
			throw new RuntimeException( ex );
		}
	}
	
	
	/**
	 * @param json JSON文字列
	 * @param ref  ジェネリック型情報
	 * @return JSON文字列をパースして、指定した型のオブジェクトインスタンスを返します。<br>
	 *         パースに失敗する場合は、ランタイム例外をスローします。
	 */
	public static <T> T parse( String json, TypeReference<T> ref ) {
		try {
			return SingletonHolder.mapper.readValue( json, ref );
		} catch ( Exception ex ) {
			throw new RuntimeException( ex );
		}
	}
	
	/**
	 * @param object オブジェクト
	 * @return 指定したオブジェクトのJSON文字列表現を返します。<br>
	 *         文字列化に失敗する場合は、ランタイム例外をスローします。
	 */
	public static String stringify( Object object ) {
		try {
			return SingletonHolder.mapper.writeValueAsString( object );
		} catch ( Exception ex ) {
			throw new RuntimeException( ex );
		}
	}
	
	
	// ユーティリティ系
	
	/**
	 * マップコンテキスト.
	 * 
	 * <p>
	 * <b>■概要：</b><br>
	 * 内部的に {@link Map<String, Object>} インスタンスを内包し、<br>
	 * {@link #put(String, Object)} メソッドで {@code thisポインタ} を返す、<br>
	 * カスケード呼び出しが可能なユーティリティオブジェクトです。<br>
	 * このオブジェクトは {@code StringBuilder} のようなカスケードコードが可能です。<br>
	 * 
	 * <pre>
	 * String json = new MapContext()
	 * 		.put( "hoge", 1 )
	 * 		.put( "moge", 2 )
	 * 		.put( "piyo", 3 )
	 * 		.stringify();
	 * </pre>
	 * 
	 * 実際には、{@link JsonMapper#map()} を用いて以下のように使用したほうが便利です。
	 * 
	 * <pre>
	 * String json = JsonMapper
	 * 		.map()
	 * 		.put( "hoge", 1 )
	 * 		.put( "moge", 2 )
	 * 		.put( "piyo", 3 )
	 * 		.stringify();
	 * </pre>
	 * </p>
	 * 
	 * @author sugaryo
	 */
	public static class MapContext {
		private Map<String, Object> map = new HashMap<>();
		
		/**
		 * @param key   putするキー
		 * @param value putする値
		 * @return 内包する {@link Map<String, Object>} インスタンスに {@link Map#put} し、{@code this} を返します。
		 * 
		 * @see HashMap#put(Object, Object)
		 */
		public MapContext put( String key, Object value ) {
			this.map.put( key, value );
			return this;
		}
		
		/**
		 * @return 内包する {@link Map<String, Object>} のJSON文字列表現を返します。
		 */
		public String stringify() {
			return JsonMapper.stringify( this.map );
		}
	}
	
	/**
	 * @return 新しい {@link MapContext} インスタンス
	 */
	public static MapContext map() {
		return new MapContext();
	}
	
	
	
	/** Initialization-on-demand holder idiom */
	private static class SingletonHolder {
		private static final ObjectMapper mapper = new ObjectMapper();
	}
}

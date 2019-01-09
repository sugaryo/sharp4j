package sharp4j.util.io;


import java.io.BufferedReader;
import java.io.IOException;

/**
 * 静かなBufferedReaderクラス
 * 
 * <p>
 * このクラスは {@link BufferedReader} に処理を委譲するだけのクラスです。<br>
 * 移譲したメソッドの検査例外を {@link RuntimeException} でラップする事で {@code throws} 宣言を外しています。
 * </p>
 * 
 * @author sugaryo
 * 
 * @see java.io.BufferedReader
 * @see sharp4j.util.io.QuietReader
 */
public class QuietBufferedReader extends QuietReader
{
	/** 処理を移譲する {@link BufferedReader} オブジェクト */
	private final BufferedReader reader;
	
	/**
	 * @param reader 処理を移譲する {@link BufferedReader} オブジェクト
	 */
	public QuietBufferedReader(BufferedReader reader)
	{
		super( reader );
		this.reader = reader;
	}

	/**
	 * @see java.io.BufferedReader#readLine()
	 */
	public String readLine()
	{
		try
		{
			return this.reader.readLine();
		}
		catch ( IOException ex )
		{
			throw new RuntimeException( ex );
		}
	}
}
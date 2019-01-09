package sharp4j.util.io;


import java.io.IOException;
import java.io.Reader;

/**
 * 静かなReaderクラス
 * 
 * <p>
 * このクラスは {@link Reader} に処理を委譲するだけのクラスです。<br>
 * 移譲したメソッドの検査例外を {@link RuntimeException} でラップする事で {@code throws} 宣言を外しています。
 * </p>
 * 
 * @author sugaryo
 * 
 * @see java.io.Reader
 */
public class QuietReader implements AutoCloseable
{
	/** 処理を移譲する {@link Reader} オブジェクト */
	protected final Reader reader;
	
	/**
	 * @param reader 処理を移譲する {@link Reader} オブジェクト
	 */
	public QuietReader(Reader reader)
	{
		this.reader = reader;
	}
	
	/**
	 * @seee java.io.Reader#read()
	 */
	public int read()
	{
		try
		{
			return this.reader.read();
		}
		catch ( IOException ex )
		{
			throw new RuntimeException( ex );
		}
	}
	
	/**
	 * @see java.io.Reader#read(char[])
	 */
	public int read(char[] cbuf)
	{
		try
		{
			return this.reader.read( cbuf );
		}
		catch ( IOException ex )
		{
			throw new RuntimeException( ex );
		}
	}
	
	/**
	 * @see java.io.Reader#markSupported()
	 */
	public boolean markSupported()
	{
		return this.reader.markSupported();
	}
	
	/**
	 * @see java.io.Reader#mark(int) 
	 */
	public void mark(int readAheadLimit)
	{
		try
		{
			this.reader.mark( readAheadLimit );
		}
		catch ( IOException ex )
		{
			throw new RuntimeException( ex );
		}
	}
	
	/**
	 * @see java.io.Reader#reset()
	 */
	public void reset()
	{
		try
		{
			this.reader.reset();
		}
		catch ( IOException ex )
		{
			throw new RuntimeException( ex );
		}
	}
	
	
	/* (非 Javadoc)
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	public void close()
	{
		try
		{
			this.reader.close();
		}
		catch ( IOException ex )
		{
			throw new RuntimeException( ex );
		}
	}
}
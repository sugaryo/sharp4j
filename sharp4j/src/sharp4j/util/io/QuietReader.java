package sharp4j.util.io;


import java.io.IOException;
import java.io.Reader;

public class QuietReader implements AutoCloseable
{
	protected final Reader reader;

	public QuietReader(Reader reader)
	{
		this.reader = reader;
	}


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

	public boolean markSupported()
	{
		return this.reader.markSupported();
	}

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
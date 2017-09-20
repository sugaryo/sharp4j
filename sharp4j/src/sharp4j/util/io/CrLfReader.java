package sharp4j.util.io;


import java.io.BufferedReader;

public class CrLfReader implements AutoCloseable
{
	private static final char CR = '\r';
	private static final char LF = '\n';
	private static final char EMPTY = '\0';
	
	private static final int BUFF_SIZE = 256;
	
	
	private final QuietBufferedReader reader;
	private final StringBuilder sb;
	
	
	private class Buffer
	{
		private final char[] temp = new char[BUFF_SIZE];
		
		private int size = 0;
		private int index = BUFF_SIZE;
		private boolean eof = false;
		
		
		public boolean fill()
		{
			if ( !eof )
			{
				size = reader.read( temp );
				index = 0;
				eof = -1 == size;
			}
			
			return !eof;
		}
		
		public char seek()
		{
			return index < size ? temp[index++] : EMPTY;
		}
	}
	
	private final Buffer buffer;
	
	
	public CrLfReader(BufferedReader reader)
	{
		this.reader = new QuietBufferedReader( reader );
		this.sb = new StringBuilder();
		this.buffer = new Buffer();
	}
	
	
	public String next()
	{
		this.sb.setLength( 0 );
		
		if ( this.read() )
		{
			return this.sb.toString();
		}
		else
		{
			return null;
		}
	}
	
	
	boolean end = false;
	
	private boolean read()
	{
		if ( this.end ) return false;
		
		boolean cr = false;
		
		do
		{
			char c;
			while ( EMPTY != (c = buffer.seek()) )
			{
				if ( cr )
				{
					// 前回のCrと今回のLfでCrLfが完成。
					if ( LF == c )
					{
						return true;
					}
					// 前回のCrを一手遅れて格納。
					else
					{
						sb.append( CR );
					}
				}
				
				// CR フラグを更新。
				cr = CR == c;
				
				if ( !cr )
				{
					sb.append( c );
				}
			}
		}
		while ( buffer.fill() );
		
		
		// fill で false が帰ってきた周回で end フラグを立てる。
		this.end = true;
		return true;
	}
	
	
	@Override
	public void close() throws Exception
	{
		this.reader.close();
	}
}

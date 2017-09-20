package sharp4j.util.io;


import java.io.BufferedReader;

public class CrLfReader implements AutoCloseable
{
	private final QuietBufferedReader reader;
	private final StringBuilder sb;
	
	
	private class Buffer
	{
		private static final int BUFF_SIZE = 256;
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
		
		public boolean next()
		{
			return index < size;
		}
		
		public char seek()
		{
			return temp[index++];
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
		
		if ( readline(false) ) return true;
		
		this.end = true;
		return true;
	}

	private static final char CR = '\r';
	private static final char LF = '\n';
	
	private boolean readline(boolean cr)
	{
		// ■バッファ読み込み済みの文字を CrLf が完成する所まで読み進める。
		while ( buffer.next() )
		{
			final char c = buffer.seek();
			
			if ( cr )
			{
				// ★ CrLf が完成 ★
				if ( LF == c ) return true;
				
				// 前回のCrを一手遅れて格納。
				sb.append( CR );
			}
			
			// CR フラグを更新。
			cr = CR == c;
			
			if ( !cr )
			{
				sb.append( c );
			}
		}
		
		// ■CrLf が完成せずバッファ読み込みループを抜けてきた場合
		
		if ( buffer.fill() )
		{
			return readline( cr ); // 再帰処理
		}
		else
		{
			return false;
		}
	}
	
	
	@Override
	public void close() throws Exception
	{
		this.reader.close();
	}
}

package sharp4j.util.io;


import java.io.BufferedReader;

public class CrLfReader implements AutoCloseable
{
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
	
	private final QuietBufferedReader reader;
	private final Buffer buffer;
	private final StringBuilder sb;
	
	
	public CrLfReader(BufferedReader reader)
	{
		this.reader = new QuietBufferedReader( reader );
		this.buffer = new Buffer();
		this.sb = new StringBuilder();
	}
	
	
	public String next()
	{
		this.sb.setLength( 0 );
		
		if ( this.build() )
		{
			return this.sb.toString();
		}
		else
		{
			return null;
		}
	}
	
	
	boolean end = false;
	
	private boolean build()
	{
		if ( this.end )
		{
			return false;
		}
		else
		{
			this.readline( false );
			return true;
		}
	}
	
	private static final char CR = '\r';
	private static final char LF = '\n';
	
	private void readline(boolean cr)
	{
		// ■バッファ読み込み済みの文字を CrLf が完成する所まで読み進める。
		while ( buffer.next() )
		{
			final char c = buffer.seek();
			
			if ( cr )
			{
				// ★ CrLf が完成 ★
				if ( LF == c ) return;
				
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
			readline( cr ); // 再帰処理
		}
		else
		{
			this.end = true;
		}
	}
	
	
	@Override
	public void close() throws Exception
	{
		this.reader.close();
	}
}

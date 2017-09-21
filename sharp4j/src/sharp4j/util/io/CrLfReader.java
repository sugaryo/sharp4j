package sharp4j.util.io;

import static sharp4j.common.JavaUtil.*;

import java.io.Reader;

public class CrLfReader implements AutoCloseable
{
	private static final int DEF_SIZE = 256;
	private static final int MIN_SIZE = 16;
	
	private class Buffer
	{
		private final char[] temp;
		
		private int size ;
		private int index;
		private boolean eof;
		
		public Buffer(int size)
		{
			this.temp = new char[max( size, MIN_SIZE )];
			this.size = 0;
			this.index = this.temp.length;
			this.eof = false;
		}

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
	
	private final QuietReader reader;
	private final Buffer buffer;
	private final StringBuilder sb;
	
	public CrLfReader(Reader reader)
	{
		this( reader, DEF_SIZE );
	}
	public CrLfReader(Reader reader, int bufferSize)
	{
		this.reader = new QuietReader( reader );
		this.buffer = new Buffer( bufferSize );
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
		// ■バッファ読み込み済みの文字を CrLf が完成する所まで読み進める：
		while ( this.buffer.next() )
		{
			final char c = this.buffer.seek();
			
			if ( cr )
			{
				// ★ CrLf が完成 ★
				if ( LF == c ) return;
				
				// CrLfが完成しなかった場合は、単独のCr出現（←そんなデータ無いと思うけど）なので
				// 前回のCrを一手遅れて格納。
				this.sb.append( CR );
			}
			
			// CR フラグを更新。
			cr = CR == c;
			
			if ( !cr )
			{
				this.sb.append( c );
			}
		}
		
		// ■CrLf が完成せずバッファ読み込みループを抜けてきた場合：
		
		if ( this.buffer.fill() )
		{
			this.readline( cr );
		}
		else
		{
			if ( cr ) {
				// ファイルに含まれる最後の文字が CR で、且つバッファの最後だった場合、
				// 他に sb に詰めるタイミングが無いのでここで詰める。
				// ※そもそも、単独出現のLFは有り得るが、単独出現のCRって有り得ないので、考慮する必要性が、、、。
				sb.append( CR );
			}
			
			this.end = true;
		}
	}
	
	
	@Override
	public void close() throws Exception
	{
		this.reader.close();
	}
}

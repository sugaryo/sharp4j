package sharp4j.util.io;

import static sharp4j.common.JavaUtil.*;

import java.io.BufferedReader;

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
	
	private final QuietBufferedReader reader;
	private final Buffer buffer;
	private final StringBuilder sb;
	
	public CrLfReader(BufferedReader reader)
	{
		this( reader, DEF_SIZE );
	}
	public CrLfReader(BufferedReader reader, int bufferSize)
	{
		this.reader = new QuietBufferedReader( reader );
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
		
		// 内部バッファにファイルから追加読み込みを試行。
		if ( this.buffer.fill() )
		{
			// 追加読み込みできた場合は再帰処理してCrLfの完成（またはEOF）を目指す。
			this.readline( cr );
		}
		// ファイル終端に達していた場合、ココまで処理したのがファイルの最終行。
		else
		{
			// end フラグを立てて処理を終わる。
			this.end = true;
		}
	}
	
	
	@Override
	public void close() throws Exception
	{
		this.reader.close();
	}
}

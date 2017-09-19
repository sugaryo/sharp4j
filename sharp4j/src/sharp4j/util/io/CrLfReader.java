package sharp4j.util.io;


import java.io.BufferedReader;

public class CrLfReader implements AutoCloseable
{
	// TODO：内部での状態管理がイケてないので、BuffとReadのステータス管理を内部クラス化したい。

	// TODO：フラグ二種は一方通行なので、実質３状態の有限オートマトンだからenumにしたい。

	private final QuietBufferedReader reader;
	private final StringBuilder sb;

	private char[] buff = new char[256];
	private int index = 256;
	private int size = 0;

	public CrLfReader(BufferedReader reader)
	{
		this.reader = new QuietBufferedReader( reader );
		this.sb = new StringBuilder();
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


	private static final char CR = '\r';
	private static final char LF = '\n';

	boolean end = false;

	private boolean read()
	{
		if ( this.end ) return false;

		boolean cr = false;

		do
		{
			for ( ; this.index < this.size; this.index++ )
			{
				char c = this.buff[this.index];

				if ( cr )
				{
					// 前回のCrと今回のLfでCrLfが完成。
					if ( LF == c )
					{
						this.index++;
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
		while ( fill() );

		// fill で false が帰ってきた周回で end フラグを立てる。
		this.end = true;
		return true;
	}

	private boolean eof = false;

	private boolean fill()
	{
		if ( eof ) return false;

		int n;
		this.size = n = reader.read( this.buff );
		this.index = 0;

		eof = -1 == n;
		return !eof;
	}


	@Override
	public void close() throws Exception
	{
		this.reader.close();
	}
}

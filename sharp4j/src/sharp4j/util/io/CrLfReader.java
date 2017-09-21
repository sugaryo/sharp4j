package sharp4j.util.io;

import static sharp4j.common.JavaUtil.*;

import java.io.Reader;

/**
 * 厳密改行リーダ
 * 
 * <p>
 * <b>■概要：</b><br>
 * CrLf({@code \r\n}) のみを改行コードとして認識するストリームリーダです。<br>
 * 単体で出現する Cr({@code \r}) 及び Lf({@code \n}) は、単なるを１文字として扱います。<br>
 * </p>
 * 
 * @author sugaryo
 *
 * @see java.lang.AutoCloseable
 * @see java.io.Reader 
 */
public class CrLfReader implements AutoCloseable
{
	/** デフォルトの内部バッファサイズ：{@value} */
	private static final int DEF_SIZE = 256;
	
	/** 指定可能な最小内部バッファサイズ：{@value} */
	private static final int MIN_SIZE = 16;
	
	/**
	 * 内部バッファ
	 * 
	 * <p>
	 * <b>■概要：</b><br>
	 * {@code char[]}バッファと、読み込みシーケンスのインデックス管理を行う内部クラスです。<br>
	 * エンクロージングインスタンスの持つ {@link #reader} を用いたファイルIO処理と、<br>
	 * 読み込んだ文字バッファに対する処理をカプセル化します。
	 * </p>
	 * 
	 * @author sugaryo
	 * 
	 * @see java.io.Reader
	 * @see sharp4j.util.io.QuietReader
	 */
	private class Buffer
	{
		/** ストリームから読み込む文字バッファ */
		private final char[] temp;
		
		/** {@link #temp}バッファ に読み込んだデータサイズ */
		private int size;
		/** 現在の {@link #temp}バッファ の読み込みインデックス */
		private int index;
		/** end of file フラグ */
		private boolean eof;
		
		/**
		 * 指定したバッファサイズで内部バッファ管理インスタンスを初期化します。
		 * 
		 * @param size バッファサイズ<br>
		 * なお、{@link CrLfReader#MIN_SIZE 既定の最小サイズ} 以下の値を指定した場合は、最小サイズが設定されます。
		 */
		public Buffer(int size)
		{
			this.temp = new char[max( size, MIN_SIZE )];
			this.size = 0;
			this.index = this.temp.length;
			this.eof = false;
		}
		
		/**
		 * バッファ読み込み試行
		 * 
		 * <p>
		 * {@link #reader} からのバッファ読み込みを行います。<br>
		 * ファイルの終端に達していた場合、{@link #eof} フラグを更新 ({@code true}) します。
		 * </p>
		 * 
		 * @return バッファ読み込みの成否を返します。<br>
		 * 内部バッファへの文字データ読み込みを行った場合は {@code true} を、<br>
		 * 読み込みデータがなかった場合（ファイル終端に到達していた場合）は {@code false} を返します。
		 * 
		 * @see java.io.Reader#read(char[])
		 */
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
		
		/**
		 * シーク可能判定
		 * 
		 * @return {@link #seek()} により次の文字読み込みが可能な場合は {@code true} を、
		 * そうでない場合は {@code false} を返します。
		 */
		public boolean next()
		{
			return index < size;
		}
		
		/**
		 * 文字のシーク取得処理
		 * 
		 * <p>
		 * 内部バッファ {@link #temp} に読み込んだ文字を１文字取得し、{@link #index} をシークさせます。<br>
		 * </p>
		 * <p>
		 * このメソッドは、必ず {@link #next()} と併用し、シーク可能な状態で呼び出します。<br>
		 * シーク不能な状態で呼び出すと {@link IndexOutOfBoundsException} が発生します。<br>
		 * </p>
		 * 
		 * @return バッファの文字を取得。
		 */
		public char seek()
		{
			return temp[index++];
		}
	}
	
	/** 文字読み込みに使用するストリームリーダ */
	private final QuietReader reader;
	
	/** 内部バッファ管理 */
	private final Buffer buffer;
	
	/** 一行ぶんの文字列構築 */
	private final StringBuilder sb;
	
	/**
	 * デフォルトの内部バッファサイズで厳密改行リーダを初期化します。
	 * 
	 * @param reader 文字読み込みに使用するリーダ
	 */
	public CrLfReader(Reader reader)
	{
		this( reader, DEF_SIZE );
	}
	/**
	 * 指定した内部バッファサイズで厳密改行リーダを初期化します。
	 * 
	 * @param reader 文字読み込みに使用するリーダ
	 * @param bufferSize 内部バッファサイズ（デフォルト：{@link #DEF_SIZE}）
	 */
	public CrLfReader(Reader reader, int bufferSize)
	{
		this.reader = new QuietReader( reader );
		this.buffer = new Buffer( bufferSize );
		this.sb = new StringBuilder();
	}
	
	
	/**
	 * 厳密改行読み込み
	 * 
	 * <p>
	 * <b>■メソッド概要：</b><br>
	 * このメソッドの基本的な使用方法は {@link BufferedReader#readLine()} と同じです。<br>
	 * <b>CrLf({@code \r\n})</b> だけを <b>1行の終端</b> として扱う点が異なります。<br>
	 * </p>
	 * <p>
	 * <b>■読み込み処理の仕様：</b><br>
	 * 行データ内に含まれる単独の <b>Cr({@code \r})</b> 及び <b>Lf({@code \n})</b> は、
	 * そのまま戻り値の {@link String} に含まれます。<br>
	 * ファイルに空行が含まれる場合は、そのまま空文字列（{@code ""}）を返します。<br>
	 * ファイルの終端に達した場合、ストップコードとして {@code null} を返します。<br>
	 * </p>
	 * 
	 * @return 
	 * 読み込んだ1行の {@link String} オブジェクトを返します。<br>
	 * ファイル終端に達している場合は {@code null} を返します。
	 * 
	 * @see java.io.BufferedReader#readLine()
	 */
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
	
	
	/** データ処理の終了フラグ */
	boolean end = false;
	
	/**
	 * 1行ぶんの文字列構築
	 * 
	 * <p>
	 * 内部バッファから文字データを取得し、{@link #sb StringBuilder} に <b>1行ぶんの文字列</b> を構築します。<br>
	 * ここで言う「1行ぶんの文字列の構築」とは、<br>
	 * CrLf({@code \r\n}) が出現するまで、<br>
	 * 若しくはファイル終端に達するまでを意味します。<br>
	 * これは、ファイルに空行が含まれる場合でも同様です。<br>
	 * </p>
	 * 
	 * @return 
	 * 文字列の構築処理を行った場合は {@code true} を返します。<br>
	 * ファイル終端に達していた場合は {@code false} を返します。<br>
	 */
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
	
	/** キャリッジリターン文字（{@code \r}） */
	private static final char CR = '\r';
	
	/** ラインフィード文字（{@code \n}） */
	private static final char LF = '\n';
	
	/**
	 * 行構築処理 <b>（再帰処理メソッド）</b>
	 * 
	 * <p>
	 * <b>■処理概要：</b><br>
	 * 内部バッファに読み込んだ文字を一文字ずつ処理し、<br>
	 * {@link #sb StringBuilder} に一行を構成する連続した文字列を構築します。<br>
	 * </p>
	 * 
	 * <p>
	 * <b>■基本的な処理の流れ：</b><br>
	 * 文字の読み込みを進めていき、改行コード CrLf({@code \r\n}) が完成した時点で処理を抜けます。<br>
	 * 内部バッファに読み込み済みの文字をすべて処理してもCrLfが完成しない場合、<br>
	 * {@link Buffer#fill()} を呼び出し、ファイルからの追加読み込みを行います。<br>
	 * <br>
	 * ファイルからの追加読み込みに成功した場合、再起処理を行いCrLfの完成を目指します。<br>
	 * <br>
	 * <b>■ファイル最終行の処理の流れ：</b><br>
	 * CrLfが完成する事なく内部バッファの処理を終え、且つファイル終端に達した場合、<br>
	 * それまで処理した文字データが「ファイルの最終行」である事を意味します。<br>
	 * この場合、処理の完了フラグ {@link #end} に {@code true} を設定して処理を終わります。<br>
	 * <br>
	 * 今回の {@link #next()} 呼び出しに対して、構築した最終行の文字列を返し、<br>
	 * 次回の {@link #next()} 呼び出し時に 処理の完了フラグ {@link #end} の {@code true} を以って、<br>
	 * 読み込み処理の完了を意味する ストップコード {@code null} を返します。
	 * </p>
	 * 
	 * 
	 * @param cr 前回処理した文字が Cr({@code \r}) か否かを示すフラグ。<br>
	 * Cr({@code \r}) に関してのみ、改行コードCrLfの一部と単独出現のCrを区別する必要があるので
	 */
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
		
		// 追加読み込みできた場合は再帰処理してCrLfの完成（またはEOF）を目指す。
		if ( this.buffer.fill() )
		{
			this.readline( cr );
		}
		// ファイル終端に達していた場合、ココまで処理したのがファイルの最終行。
		else
		{
			// ファイルに含まれる最後の文字が CR で、且つバッファの最後だった場合、
			// 他に sb に詰めるタイミングが無いのでここで詰める。
			if ( cr ) sb.append( CR );
			
			this.end = true;
		}
	}
	
	
	/* (非 Javadoc)
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	public void close() throws Exception
	{
		this.reader.close();
	}
}

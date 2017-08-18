package sharp4j.core;

// commons-lang 使えば良いじゃんってのはあるけど、外部jar非依存にしたかったのと、自分好みにアレしたかったので。

public final class JdkUtil extends StaticClass
{
	public static final <T> T nvl(T value, T alter)
	{
		return null != value ? value : alter;
	}

	public static String nvl(String value)
	{
		return nvl( value, "" );
	}

	public static final boolean isEmpty(String s)
	{
		return null == s || s.isEmpty();
	}

	public static final boolean notEmpty(String s)
	{
		return !isEmpty( s );
	}


	public static final int min( int x, int y )
	{
		return x < y ? x : y;
	}
	public static final long min( long x, long y )
	{
		return x < y ? x : y;
	}
	public static final <T extends Comparable<T>> T min(T x, T y) {
		return x.compareTo( y ) < 0 ? x : y;
	}

	public static final int max( int x, int y )
	{
		return x > y ? x : y;
	}
	public static final long max( long x, long y )
	{
		return x > y ? x : y;
	}
	public static final <T extends Comparable<T>> T max(T x, T y) {
		return x.compareTo( y ) > 0 ? x : y;
	}



}

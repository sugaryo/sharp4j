package sharp4j.common;


public final class JavaUtil
{
	public static <T> T nvl(T value, T alter)
	{
		return null != value ? value : alter;
	}
	
	public static String nvl(String value)
	{
		return nvl( value, "" );
	}
	
	public static int nvl(Integer value)
	{
		return nvl( value, 0 ).intValue();
	}
	
	public static long nvl(Long value)
	{
		return nvl( value, 0 ).longValue();
	}
	
	// いわゆる Math系：
	
	public static <T extends Comparable<T>> T max(T x, T y)
	{
		return x.compareTo( y ) > 0 ? x : y;
	}
	
	public static int max(int x, int y)
	{
		return x > y ? x : y;
	}
	
	public static long max(long x, long y)
	{
		return x > y ? x : y;
	}
	
	
	public static <T extends Comparable<T>> T min(T x, T y)
	{
		return x.compareTo( y ) < 0 ? x : y;
	}
	
	public static int min(int x, int y)
	{
		return x < y ? x : y;
	}
	
	public static long min(long x, long y)
	{
		return x < y ? x : y;
	}
	
	
	// いわゆる StringUtils系：
	
	public static boolean isEmpty(String value)
	{
		return null == value || value.isEmpty();
	}
	
	public static boolean notEmpty(String value)
	{
		return !isEmpty( value );
	}
	
	
	public static String join(String by, Iterable<String> values)
	{
		StringBuilder sb = new StringBuilder();
		
		String add = "";
		for ( String value : values )
		{
			sb.append( add );
			sb.append( value );
			add = by;
		}
		
		return sb.toString();
	}
	
	public static String join(String by, String... values)
	{
		StringBuilder sb = new StringBuilder();
		
		String add = "";
		for ( String value : values )
		{
			sb.append( add );
			sb.append( value );
			add = by;
		}
		
		return sb.toString();
	}
}
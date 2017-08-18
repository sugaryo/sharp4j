package sharp4j.core;

abstract class StaticClass
{
	StaticClass()
	{
		throw new UnsupportedException();
	}
	// 厳密な意味での static class がJavaでは実装できないので、C/C++時代の感じで。
	// ただ、protectedにしちゃうと派生クラス側で
}

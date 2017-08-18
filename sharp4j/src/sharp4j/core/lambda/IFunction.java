package sharp4j.core.lambda;

public interface IFunction<TParameter, TResult>
{
	public TResult function(TParameter parameter);
}

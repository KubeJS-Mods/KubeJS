package dev.latvian.kubejs.util;

import java.util.HashMap;
import java.util.function.Function;

/**
 * @author LatvianModder
 */
public class DynamicMapJS<T> extends HashMap<String, T> implements WrappedJS
{
	private final Function<String, ? extends T> objectProvider;

	public DynamicMapJS(Function<String, ? extends T> o)
	{
		objectProvider = o;
	}

	@Override
	public T get(Object key)
	{
		return super.computeIfAbsent(key.toString(), objectProvider);
	}

	@Override
	public boolean containsKey(Object name)
	{
		return true;
	}
}
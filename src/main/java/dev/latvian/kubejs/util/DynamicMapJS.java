package dev.latvian.kubejs.util;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.function.Function;

/**
 * @author LatvianModder
 */
public class DynamicMapJS<K, V> extends HashMap<K, V> implements WrappedJS
{
	private final Function<K, ? extends V> objectProvider;

	public DynamicMapJS(Function<K, ? extends V> o)
	{
		objectProvider = o;
	}

	@Override
	@Nonnull
	public V get(Object key)
	{
		return super.computeIfAbsent((K) key, objectProvider);
	}

	@Override
	public boolean containsKey(Object name)
	{
		return true;
	}
}
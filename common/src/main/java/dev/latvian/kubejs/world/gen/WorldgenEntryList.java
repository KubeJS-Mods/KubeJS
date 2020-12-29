package dev.latvian.kubejs.world.gen;

import dev.latvian.kubejs.util.ListJS;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class WorldgenEntryList<T>
{
	public Object values = null;
	public boolean blacklist = false;

	public boolean verify(Function<Object, T> factory, Predicate<T> filter)
	{
		if (values == null)
		{
			return true;
		}

		for (Object v : ListJS.orSelf(values))
		{
			if (filter.test(factory.apply(v)))
			{
				return !blacklist;
			}
		}

		return blacklist;
	}
}

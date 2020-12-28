package dev.latvian.kubejs.world.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class WorldgenEntryList<T>
{
	public List<T> values = new ArrayList<>();
	public boolean blacklist = false;

	public <E> WorldgenEntryList<E> map(Function<T, E> function)
	{
		WorldgenEntryList<E> l = new WorldgenEntryList<>();
		l.blacklist = blacklist;

		for (T t : values)
		{
			l.values.add(function.apply(t));
		}

		return l;
	}

	public WorldgenEntryList()
	{
	}

	public WorldgenEntryList(T item)
	{
		values.add(item);
	}

	public void add(T item)
	{
		values.add(item);
	}

	public void add(T[] items)
	{
		for (T item : items)
		{
			add(item);
		}
	}

	public boolean verify(Predicate<T> filter)
	{
		if (values.isEmpty())
		{
			return true;
		}

		for (T value : values)
		{
			if (filter.test(value) != blacklist)
			{
				return !blacklist;
			}
		}

		return blacklist;
	}
}

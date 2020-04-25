package dev.latvian.kubejs.util;

import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class CountingMap
{
	public static class Entry implements Comparable<Entry>
	{
		public final Object key;
		public final long value;

		public Entry(Object k, long v)
		{
			key = k;
			value = v;
		}

		public Entry(Object2LongOpenHashMap.Entry<Object> entry)
		{
			key = entry.getKey();
			value = entry.getLongValue();
		}

		@Override
		public int compareTo(Entry o)
		{
			int c = Long.compare(o.value, value);

			if (c == 0 && key != null && o.key != null)
			{
				c = key.toString().compareToIgnoreCase(o.key.toString());
			}

			return c;
		}
	}

	private final Object2LongOpenHashMap<Object> map;

	public CountingMap()
	{
		map = new Object2LongOpenHashMap<>();
		map.defaultReturnValue(0L);
	}

	public long get(Object key)
	{
		return map.getLong(key);
	}

	public long set(Object key, long value)
	{
		if (value <= 0L)
		{
			return map.removeLong(key);
		}
		else
		{
			return map.put(key, value);
		}
	}

	public long add(Object key, long value)
	{
		return set(key, get(key) + value);
	}

	public void clear()
	{
		map.clear();
	}

	public int getSize()
	{
		return map.size();
	}

	public void forEach(Consumer<Entry> forEach)
	{
		map.object2LongEntrySet().forEach(entry -> forEach.accept(new Entry(entry)));
	}

	public List<Entry> getEntries()
	{
		List<Entry> list = new ArrayList<>(map.size());
		forEach(list::add);
		return list;
	}

	public Set<Object> getKeys()
	{
		return map.keySet();
	}

	public Collection<Long> getValues()
	{
		return map.values();
	}

	public long getTotalCount()
	{
		final long[] count = {0L};
		forEach(entry -> count[0] += entry.value);
		return count[0];
	}
}
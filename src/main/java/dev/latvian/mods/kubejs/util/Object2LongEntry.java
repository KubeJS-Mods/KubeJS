package dev.latvian.mods.kubejs.util;

import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;

public class Object2LongEntry implements Comparable<Object2LongEntry> {
	public final Object key;
	public final long value;

	public Object2LongEntry(Object k, long v) {
		key = k;
		value = v;
	}

	public Object2LongEntry(Object2LongOpenHashMap.Entry<Object> entry) {
		key = entry.getKey();
		value = entry.getLongValue();
	}

	@Override
	public int compareTo(Object2LongEntry o) {
		int c = Long.compare(o.value, value);

		if (c == 0 && key != null && o.key != null) {
			c = key.toString().compareToIgnoreCase(o.key.toString());
		}

		return c;
	}
}
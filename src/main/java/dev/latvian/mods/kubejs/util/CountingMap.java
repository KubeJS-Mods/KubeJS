package dev.latvian.mods.kubejs.util;

import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class CountingMap {
	private final Object2LongOpenHashMap<Object> map;

	public CountingMap() {
		map = new Object2LongOpenHashMap<>();
		map.defaultReturnValue(0L);
	}

	public long get(Object key) {
		return map.getLong(key);
	}

	public long set(Object key, long value) {
		if (value <= 0L) {
			return map.removeLong(key);
		} else {
			return map.put(key, value);
		}
	}

	public long add(Object key, long value) {
		return set(key, get(key) + value);
	}

	public void clear() {
		map.clear();
	}

	public int getSize() {
		return map.size();
	}

	public void forEach(Consumer<Object2LongEntry> forEach) {
		map.object2LongEntrySet().forEach(entry -> forEach.accept(new Object2LongEntry(entry)));
	}

	public List<Object2LongEntry> getEntries() {
		List<Object2LongEntry> list = new ArrayList<>(map.size());
		forEach(list::add);
		return list;
	}

	public Set<Object> getKeys() {
		return map.keySet();
	}

	public Collection<Long> getValues() {
		return map.values();
	}

	public long getTotalCount() {
		final long[] count = {0L};
		forEach(entry -> count[0] += entry.value);
		return count[0];
	}
}
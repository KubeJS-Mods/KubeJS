package dev.latvian.mods.kubejs.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public record TinyMap<K, V>(TinyMap.Entry<K, V>[] entries) {
	public record Entry<K, V>(K key, V value) {
	}

	@SuppressWarnings({"unchecked", "ToArrayCallWithZeroLengthArrayArgument"})
	public TinyMap(Collection<Entry<K, V>> collection) {
		this(collection.toArray(new Entry[collection.size()]));
	}

	public TinyMap(TinyMap<K, V> map) {
		this(map.entries.clone());
	}

	public boolean isEmpty() {
		return entries.length == 0;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> TinyMap<K, V> ofMap(Map<K, V> map) {
		var entries = new Entry[map.size()];
		int i = 0;
		for (var entry : map.entrySet()) {
			entries[i++] = new Entry<>(entry.getKey(), entry.getValue());
		}
		return new TinyMap<K, V>(entries);
	}

	public Map<K, V> toMap() {
		var map = new HashMap<K, V>(entries.length);

		for (var entry : entries) {
			map.put(entry.key(), entry.value());
		}

		return map;
	}
}
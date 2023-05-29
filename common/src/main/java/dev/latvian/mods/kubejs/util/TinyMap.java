package dev.latvian.mods.kubejs.util;

import java.util.Collection;

public record TinyMap<K, V>(TinyMap.Entry<K, V>[] entries) {
	public record Entry<K, V>(K key, V value) {
	}

	@SuppressWarnings("unchecked")
	public TinyMap(Collection<Entry<K, V>> collection) {
		this(collection.toArray(new Entry[0]));
	}

	public TinyMap(TinyMap<K, V> map) {
		this(new Entry[map.entries.length]);
		System.arraycopy(map.entries, 0, entries, 0, entries.length);
	}

	public boolean isEmpty() {
		return entries.length == 0;
	}
}
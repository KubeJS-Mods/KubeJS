package dev.latvian.mods.kubejs.util;

import java.util.Collection;

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
}
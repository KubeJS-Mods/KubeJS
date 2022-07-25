package dev.latvian.mods.kubejs.util;

import java.util.HashMap;

/**
 * @author LatvianModder
 */
public class AttachedData<T> extends HashMap<String, Object> {
	private final T parent;

	public AttachedData(T p) {
		parent = p;
	}

	public T getParent() {
		return parent;
	}

	public void add(String key, Object data) {
		put(key, data);
	}
}
package dev.latvian.mods.kubejs.util;

/**
 * @author LatvianModder
 */
@FunctionalInterface
public interface ChangeListener<T> {
	void onChanged(T o);
}
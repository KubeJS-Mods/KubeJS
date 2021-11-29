package dev.latvian.mods.kubejs.util;

/**
 * @author LatvianModder
 */
@FunctionalInterface
public interface WrappedJSObjectChangeListener<T> {
	void onChanged(T o);
}
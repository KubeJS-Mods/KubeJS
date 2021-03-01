package dev.latvian.kubejs.util;

/**
 * @author LatvianModder
 */
@FunctionalInterface
public interface WrappedJSObjectChangeListener<T> {
	void onChanged(T o);
}
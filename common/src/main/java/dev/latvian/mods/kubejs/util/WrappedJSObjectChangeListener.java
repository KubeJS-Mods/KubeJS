package dev.latvian.mods.kubejs.util;

@FunctionalInterface
public interface WrappedJSObjectChangeListener<T> {
	void onChanged(T o);
}
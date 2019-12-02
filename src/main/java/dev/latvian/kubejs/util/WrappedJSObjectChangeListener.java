package dev.latvian.kubejs.util;

import dev.latvian.kubejs.documentation.Ignore;

/**
 * @author LatvianModder
 */
@FunctionalInterface
public interface WrappedJSObjectChangeListener<T>
{
	@Ignore
	void onChanged(T o);
}
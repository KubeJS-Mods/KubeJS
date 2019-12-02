package dev.latvian.kubejs.util;

import dev.latvian.kubejs.documentation.Ignore;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public interface WrappedJSObjectChangeListener
{
	@Ignore
	void onChanged(@Nullable Object o);
}
package dev.latvian.kubejs.util;

import jdk.nashorn.api.scripting.AbstractJSObject;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class FunctionBinding extends AbstractJSObject implements WrappedJS
{
	@FunctionalInterface
	public interface Handler
	{
		@Nullable
		Object call(Object[] args);
	}

	private Handler handler;

	public FunctionBinding(Handler h)
	{
		handler = h;
	}

	@Override
	@Nullable
	public Object call(Object thiz, Object... args)
	{
		return handler.call(args);
	}
}
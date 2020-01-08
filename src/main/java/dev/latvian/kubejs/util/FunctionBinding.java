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

	public FunctionBinding(Runnable noArgFunction)
	{
		handler = args -> {
			noArgFunction.run();
			return null;
		};
	}

	@Override
	@Nullable
	public Object call(Object thiz, Object... args)
	{
		return handler.call(args);
	}

	@Override
	public boolean isFunction()
	{
		return true;
	}
}
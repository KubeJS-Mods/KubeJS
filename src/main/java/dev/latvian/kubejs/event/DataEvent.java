package dev.latvian.kubejs.event;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class DataEvent extends EventJS
{
	private final boolean canCancel;
	private final Object data;

	public DataEvent(boolean c, @Nullable Object d)
	{
		canCancel = c;
		data = d;
	}

	@Override
	public boolean canCancel()
	{
		return canCancel;
	}

	@Nullable
	public Object getData()
	{
		return data;
	}
}
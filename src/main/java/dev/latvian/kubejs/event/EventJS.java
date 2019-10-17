package dev.latvian.kubejs.event;

import dev.latvian.kubejs.documentation.Ignore;

/**
 * @author LatvianModder
 */
public class EventJS
{
	private boolean cancelled = false;

	public boolean canCancel()
	{
		return false;
	}

	public final void cancel()
	{
		cancelled = true;
	}

	@Ignore
	public final boolean isCancelled()
	{
		return cancelled;
	}
}
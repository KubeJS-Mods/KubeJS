package com.latmod.mods.kubejs.events;

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

	public final boolean isCancelled()
	{
		return cancelled;
	}
}
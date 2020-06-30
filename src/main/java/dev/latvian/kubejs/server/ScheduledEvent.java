package dev.latvian.kubejs.server;

import dev.latvian.kubejs.script.ScriptFile;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class ScheduledEvent
{
	public final ScriptFile file;

	private final ServerJS server;
	private final boolean usingTicks;
	private final long timer;
	private final long endTime;
	private final Object data;
	private final IScheduledEventCallback callback;

	ScheduledEvent(ServerJS s, boolean ut, long t, long e, @Nullable Object d, IScheduledEventCallback c)
	{
		file = s.serverScriptManager.scriptManager.currentFile;
		usingTicks = ut;
		server = s;
		timer = t;
		endTime = e;
		data = d;
		callback = c;
	}

	public boolean isUsingTicks()
	{
		return usingTicks;
	}

	public ServerJS getServer()
	{
		return server;
	}

	public long getTimer()
	{
		return timer;
	}

	public long getEndTime()
	{
		return endTime;
	}

	@Nullable
	public Object getData()
	{
		return data;
	}

	public void reschedule()
	{
		reschedule(timer);
	}

	public long getTimerDuration()
	{
		return endTime - timer;
	}

	public ScheduledEvent reschedule(long timer)
	{
		if (isUsingTicks())
		{
			return server.scheduleInTicks(timer, data, callback);
		}
		else
		{
			return server.schedule(timer, data, callback);
		}
	}

	void call()
	{
		callback.onCallback(this);
	}
}
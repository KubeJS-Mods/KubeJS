package dev.latvian.kubejs.world;

import dev.latvian.kubejs.ScriptManager;
import dev.latvian.kubejs.util.ScriptFile;
import dev.latvian.kubejs.util.ServerJS;

/**
 * @author LatvianModder
 */
public class ScheduledEvent
{
	public final ServerJS server;
	public final transient ScriptFile file;
	public final long timer;
	public final long endTime;
	private final IScheduledEventCallback callback;

	public ScheduledEvent(ServerJS s, long t, IScheduledEventCallback c)
	{
		server = s;
		file = ScriptManager.instance.currentFile;
		timer = t;
		endTime = System.currentTimeMillis() + timer;
		callback = c;
	}

	public void reschedule()
	{
		reschedule(timer);
	}

	public ScheduledEvent reschedule(long timer)
	{
		return server.schedule(timer, callback);
	}

	void call()
	{
		callback.onCallback(this);
	}

	public long remainingTime()
	{
		return endTime - System.currentTimeMillis();
	}
}
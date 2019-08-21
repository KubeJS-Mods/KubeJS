package dev.latvian.kubejs.world;

import dev.latvian.kubejs.KubeJS;
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
	private final IScheduledEventCallback function;

	public ScheduledEvent(ServerJS s, long t, IScheduledEventCallback c)
	{
		server = s;
		file = KubeJS.currentFile;
		timer = t;
		endTime = System.currentTimeMillis() + timer;
		function = c;
	}

	public void reschedule()
	{
		reschedule(timer);
	}

	public ScheduledEvent reschedule(long timer)
	{
		return server.schedule(timer, function);
	}

	public void call()
	{
		function.onCallback(this);
	}

	public long remainingTime()
	{
		return endTime - System.currentTimeMillis();
	}
}
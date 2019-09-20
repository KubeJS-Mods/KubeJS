package dev.latvian.kubejs.server;

import dev.latvian.kubejs.documentation.Ignore;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.script.ScriptFile;
import dev.latvian.kubejs.script.ScriptManager;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class ScheduledEvent
{
	@Ignore
	public final ScriptFile file;

	private final ServerJS server;
	private final long timer;
	private final long endTime;
	private final Object data;
	private final IScheduledEventCallback callback;

	public ScheduledEvent(ServerJS s, long t, long e, @Nullable Object d, IScheduledEventCallback c)
	{
		file = ScriptManager.instance.currentFile;
		server = s;
		timer = t;
		endTime = e;
		data = d;
		callback = c;
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

	public ScheduledEvent reschedule(@P("timer") long timer)
	{
		return server.schedule(timer, data, callback);
	}

	void call()
	{
		callback.onCallback(this);
	}
}
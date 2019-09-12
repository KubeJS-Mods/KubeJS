package dev.latvian.kubejs.server;

import dev.latvian.kubejs.documentation.DocClass;
import dev.latvian.kubejs.documentation.DocMethod;
import dev.latvian.kubejs.documentation.Param;
import dev.latvian.kubejs.script.ScriptFile;
import dev.latvian.kubejs.script.ScriptManager;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
@DocClass
public class ScheduledEvent
{
	private final ServerJS server;
	private final long timer;
	private final long endTime;
	private final Object data;

	public final transient ScriptFile file;
	private final IScheduledEventCallback callback;

	public ScheduledEvent(ServerJS s, long t, long e, @Nullable Object d, IScheduledEventCallback c)
	{
		server = s;
		file = ScriptManager.instance.currentFile;
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

	@DocMethod
	public void reschedule()
	{
		reschedule(timer);
	}

	@DocMethod(params = @Param("timer"))
	public ScheduledEvent reschedule(long timer)
	{
		return server.schedule(timer, data, callback);
	}

	void call()
	{
		callback.onCallback(this);
	}

	@DocMethod
	public long remainingTime()
	{
		return endTime - System.currentTimeMillis();
	}
}
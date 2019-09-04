package dev.latvian.kubejs.server;

import dev.latvian.kubejs.documentation.DocClass;
import dev.latvian.kubejs.documentation.DocField;
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
	@DocField
	public final ServerJS server;

	@DocField
	public final long timer;

	@DocField
	public final long endTime;

	@DocField
	public final Object data;

	public final transient ScriptFile file;
	private final IScheduledEventCallback callback;

	public ScheduledEvent(ServerJS s, long t, @Nullable Object d, IScheduledEventCallback c)
	{
		server = s;
		file = ScriptManager.instance.currentFile;
		timer = t;
		endTime = System.currentTimeMillis() + timer;
		data = d;
		callback = c;
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
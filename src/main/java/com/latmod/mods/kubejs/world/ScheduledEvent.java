package com.latmod.mods.kubejs.world;

import com.latmod.mods.kubejs.util.ServerJS;
import jdk.nashorn.api.scripting.JSObject;

/**
 * @author LatvianModder
 */
public class ScheduledEvent
{
	public final ServerJS serverJS;
	public final long timer;
	public final long endTime;
	public final JSObject function;

	public ScheduledEvent(ServerJS s, long t, JSObject c)
	{
		serverJS = s;
		timer = t;
		endTime = System.currentTimeMillis() + timer;
		function = c;
	}

	public void reschedule()
	{
		reschedule(timer);
	}

	public void reschedule(long timer)
	{
		serverJS.schedule(timer, function);
	}
}
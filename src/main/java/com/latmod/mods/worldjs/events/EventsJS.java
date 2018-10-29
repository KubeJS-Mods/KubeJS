package com.latmod.mods.worldjs.events;

import com.latmod.mods.worldjs.WorldJSEventRegistryEvent;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import net.minecraftforge.common.MinecraftForge;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author LatvianModder
 */
public enum EventsJS
{
	INSTANCE;

	private static class ScriptFunctionHandler implements IEventHandler
	{
		private final ScriptObjectMirror function;

		public ScriptFunctionHandler(ScriptObjectMirror f)
		{
			function = f;
		}

		@Override
		public void onEvent(EventJS event)
		{
			function.call(function, event);
		}
	}

	private final Map<String, List<IEventHandler>> map = new Object2ObjectOpenHashMap<>();
	private Set<String> registeredIDs;

	public void listen(String id, IEventHandler handler)
	{
		List<IEventHandler> list = map.get(id);

		if (list == null)
		{
			list = new ObjectArrayList<>();
			map.put(id, list);
		}

		list.add(handler);
	}

	public void listen(String id, ScriptObjectMirror handler)
	{
		listen(id, new ScriptFunctionHandler(handler));
	}

	public boolean post(String id, EventJS event)
	{
		List<IEventHandler> list = map.get(id);

		if (list != null)
		{
			boolean c = event.canCancel();

			for (IEventHandler handler : list)
			{
				handler.onEvent(event);

				if (c && event.isCancelled())
				{
					return true;
				}
			}
		}

		return false;
	}

	public void clear()
	{
		map.clear();
		registeredIDs = null;
	}

	public Set<String> list()
	{
		if (registeredIDs == null)
		{
			registeredIDs = new ObjectOpenHashSet<>();
			MinecraftForge.EVENT_BUS.post(new WorldJSEventRegistryEvent(registeredIDs::add));
			registeredIDs = Collections.unmodifiableSet(registeredIDs);
		}

		return registeredIDs;
	}
}
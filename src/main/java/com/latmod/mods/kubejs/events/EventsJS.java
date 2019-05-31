package com.latmod.mods.kubejs.events;

import com.latmod.mods.kubejs.KubeJS;
import com.latmod.mods.kubejs.KubeJSEventRegistryEvent;
import com.latmod.mods.kubejs.util.UtilsJS;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraftforge.common.MinecraftForge;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public enum EventsJS
{
	INSTANCE;

	private final Map<String, List<IEventHandler>> map = new Object2ObjectOpenHashMap<>();
	private Map<String, Class> registeredIDs;

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

	public Map<String, Class> list()
	{
		if (registeredIDs == null)
		{
			registeredIDs = new Object2ObjectOpenHashMap<>();
			MinecraftForge.EVENT_BUS.post(new KubeJSEventRegistryEvent(registeredIDs::put));
			registeredIDs = Collections.unmodifiableMap(registeredIDs);
		}

		return registeredIDs;
	}

	public void printAllEvents()
	{
		List<String> list = new ObjectArrayList<>();

		for (Map.Entry<String, Class> entry : list().entrySet())
		{
			list.add(entry.getKey() + ": " + UtilsJS.INSTANCE.listFieldsAndMethods(entry.getValue(), 0, "isCancelled()", "cancel()", "canCancel()"));
		}

		list.sort(null);

		for (String string : list)
		{
			KubeJS.LOGGER.info(string);
		}
	}
}
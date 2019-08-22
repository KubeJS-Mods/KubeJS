package dev.latvian.kubejs.events;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEventRegistryEvent;
import dev.latvian.kubejs.ScriptManager;
import dev.latvian.kubejs.util.ScriptFile;
import dev.latvian.kubejs.util.UtilsJS;
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

	private final Map<String, List<ScriptEventHandler>> map = new Object2ObjectOpenHashMap<>();
	private Map<String, Class> registeredIDs;

	private static class ScriptEventHandler
	{
		private final ScriptFile file;
		private final IEventHandler handler;

		private ScriptEventHandler(ScriptFile f, IEventHandler h)
		{
			file = f;
			handler = h;
		}
	}

	public void listen(String id, IEventHandler handler)
	{
		List<ScriptEventHandler> list = INSTANCE.map.get(id);

		if (list == null)
		{
			list = new ObjectArrayList<>();
			INSTANCE.map.put(id, list);
		}

		list.add(new ScriptEventHandler(ScriptManager.instance.currentFile, handler));
	}

	public boolean post(String id, EventJS event)
	{
		List<ScriptEventHandler> list = map.get(id);

		if (list != null)
		{
			boolean c = event.canCancel();

			for (ScriptEventHandler handler : list)
			{
				ScriptManager.instance.currentFile = handler.file;

				try
				{
					handler.handler.onEvent(event);

					if (c && event.isCancelled())
					{
						ScriptManager.instance.currentFile = null;
						return true;
					}
				}
				catch (Exception ex)
				{
					KubeJS.LOGGER.error("Error occurred while firing '" + id + "' event in " + handler.file.path + ": " + ex);
					ex.printStackTrace();
				}
			}
		}

		ScriptManager.instance.currentFile = null;
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
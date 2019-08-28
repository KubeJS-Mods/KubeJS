package dev.latvian.kubejs.event;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.script.ScriptFile;
import dev.latvian.kubejs.script.ScriptManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

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
		return postToHandlers(id, handlers(id), event);
	}

	public List<ScriptEventHandler> handlers(String id)
	{
		List<ScriptEventHandler> list = map.get(id);
		return list == null ? Collections.emptyList() : list;
	}

	public boolean postToHandlers(String id, List<ScriptEventHandler> list, EventJS event)
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

		ScriptManager.instance.currentFile = null;
		return false;
	}

	public void clear()
	{
		map.clear();
	}
}
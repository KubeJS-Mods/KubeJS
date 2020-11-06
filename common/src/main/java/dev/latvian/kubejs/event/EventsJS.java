package dev.latvian.kubejs.event;

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
public class EventsJS
{
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

	public final ScriptManager scriptManager;
	private final Map<String, List<ScriptEventHandler>> map;

	public EventsJS(ScriptManager t)
	{
		scriptManager = t;
		map = new Object2ObjectOpenHashMap<>();
	}

	public void listen(String id, IEventHandler handler)
	{
		List<ScriptEventHandler> list = map.get(id);

		if (list == null)
		{
			list = new ObjectArrayList<>();
			map.put(id, list);
		}

		list.add(new ScriptEventHandler(scriptManager.currentFile, handler));
	}

	public List<ScriptEventHandler> handlers(String id)
	{
		List<ScriptEventHandler> list = map.get(id);
		return list == null ? Collections.emptyList() : list;
	}

	public boolean postToHandlers(String id, List<ScriptEventHandler> list, EventJS event)
	{
		if (list.isEmpty())
		{
			return false;
		}

		boolean c = event.canCancel();

		for (ScriptEventHandler handler : list)
		{
			scriptManager.currentFile = handler.file;

			try
			{
				handler.handler.onEvent(event);

				if (c && event.isCancelled())
				{
					//ScriptManager.instance.currentFile = null;
					return true;
				}
			}
			catch (Throwable ex)
			{
				if (ex.getClass().getName().equals("jdk.nashorn.api.scripting.NashornException"))
				{
					handler.file.pack.manager.type.console.error("Error occurred while firing '" + id + "' event in " + handler.file.info.location + ": " + ex);
				}
				else
				{
					ex.printStackTrace();
				}
			}
		}

		//ScriptManager.instance.currentFile = null;
		return false;
	}

	public void clear()
	{
		map.clear();
	}
}
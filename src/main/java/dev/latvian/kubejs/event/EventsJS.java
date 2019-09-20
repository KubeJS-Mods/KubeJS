package dev.latvian.kubejs.event;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.script.ScriptFile;
import dev.latvian.kubejs.script.ScriptManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import jdk.nashorn.api.scripting.NashornException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class EventsJS
{
	@Deprecated
	public static EventsJS INSTANCE = new EventsJS();

	private static final Map<String, List<ScriptEventHandler>> map = new Object2ObjectOpenHashMap<>();

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

	public static void listen(String id, IEventHandler handler)
	{
		List<ScriptEventHandler> list = map.get(id);

		if (list == null)
		{
			list = new ObjectArrayList<>();
			map.put(id, list);
		}

		list.add(new ScriptEventHandler(ScriptManager.instance.currentFile, handler));
	}

	public static boolean post(String id, EventJS event)
	{
		return postToHandlers(id, handlers(id), event);
	}

	public static boolean postDouble(String id, String extra, EventJS event)
	{
		if (!post(id + "." + extra, event))
		{
			return post(id, event);
		}

		return true;
	}

	public static List<ScriptEventHandler> handlers(String id)
	{
		List<ScriptEventHandler> list = map.get(id);
		return list == null ? Collections.emptyList() : list;
	}

	public static boolean postToHandlers(String id, List<ScriptEventHandler> list, EventJS event)
	{
		if (list.isEmpty())
		{
			return false;
		}

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
			catch (NashornException ex)
			{
				KubeJS.LOGGER.error("Error occurred while firing '" + id + "' event in " + (handler.file == null ? "Unknown file" : handler.file.getPath()) + ": " + ex);
			}
			catch (Throwable ex)
			{
				ex.printStackTrace();
			}
		}

		ScriptManager.instance.currentFile = null;
		return false;
	}

	public static void clear()
	{
		map.clear();
	}
}
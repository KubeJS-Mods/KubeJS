package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.rhino.RhinoException;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class EventsJS {
	private static class ScriptEventHandler {
		private final IEventHandler handler;

		private ScriptEventHandler(IEventHandler h) {
			handler = h;
		}
	}

	public final ScriptManager scriptManager;
	private final Map<String, List<ScriptEventHandler>> map;

	public EventsJS(ScriptManager t) {
		scriptManager = t;
		map = new Object2ObjectOpenHashMap<>();
	}

	public void listen(String id, IEventHandler handler) {
		id = id.replace("yeet", "remove");
		var list = map.get(id);

		if (list == null) {
			list = new ObjectArrayList<>();
			map.put(id, list);
		}

		list.add(new ScriptEventHandler(handler));
	}

	public List<ScriptEventHandler> handlers(String id) {
		var list = map.get(id);
		return list == null ? Collections.emptyList() : list;
	}

	public boolean postToHandlers(String id, List<ScriptEventHandler> list, EventJS event) {
		if (list.isEmpty()) {
			return false;
		}

		var c = event.canCancel();

		for (var handler : list) {
			try {
				handler.handler.onEvent(event);

				if (c && event.isCanceled()) {
					return true;
				}
			} catch (RhinoException ex) {
				scriptManager.type.console.error("Error occurred while handling event '" + id + "': " + ex.getMessage());
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}

		//ScriptManager.instance.currentFile = null;
		return false;
	}

	public void clear() {
		map.clear();
	}
}
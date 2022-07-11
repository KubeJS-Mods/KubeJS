package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.script.ScriptType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EventMap extends HashMap<String, EventMap.EventHandlerWrapper> {
	public record EventHandlerWrapper(ScriptType scriptType, EventHandler eventHandler) {
		public static final EventHandlerWrapper NONE = new EventHandlerWrapper(null, null);

		public void listen(IEventHandler handler) {
			if (eventHandler != null) {
				eventHandler.listen(scriptType, handler);
			}
		}

		public void listen(String sub, IEventHandler handler) {
			if (eventHandler != null) {
				eventHandler.of(sub).listen(scriptType, handler);
			}
		}
	}

	private final ScriptType scriptType;
	private final EventGroup group;
	private final Map<String, EventHandlerWrapper> handlers;

	public EventMap(ScriptType scriptType, EventGroup group) {
		this.scriptType = scriptType;
		this.group = group;
		this.handlers = new HashMap<>();
	}

	@Override
	public EventHandlerWrapper get(Object key) {
		EventHandlerWrapper wrapper = handlers.get(key);

		if (wrapper == null) {
			var handler = group.getHandlers().get(key);

			if (handler == null) {
				scriptType.console.pushLineNumber();
				scriptType.console.error("Unknown event '" + key + "'!");
				scriptType.console.popLineNumber();
				return EventHandlerWrapper.NONE;
			} else {
				wrapper = new EventHandlerWrapper(scriptType, handler);
				handlers.put(String.valueOf(key), wrapper);
			}
		}

		return wrapper;
	}

	@Override
	public boolean containsKey(Object key) {
		return true;
	}

	@Override
	public Set<String> keySet() {
		return handlers.keySet();
	}
}

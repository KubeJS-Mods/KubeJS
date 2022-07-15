package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EventMap extends HashMap<String, EventMap.EventHandlerWrapper> {
	public static final class EventHandlerWrapper extends BaseFunction {
		public static final EventHandlerWrapper NONE = new EventHandlerWrapper(null, null);

		private final ScriptType scriptType;
		private final EventHandler eventHandler;

		public EventHandlerWrapper(ScriptType scriptType, EventHandler eventHandler) {
			this.scriptType = scriptType;
			this.eventHandler = eventHandler;
		}

		@Override
		public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
			if (eventHandler != null) {
				if (args.length == 1) {
					eventHandler.listen(scriptType, null, (IEventHandler) Context.jsToJava(args[0], IEventHandler.class));
				} else if (args.length == 2) {
					var handler = (IEventHandler) Context.jsToJava(args[1], IEventHandler.class);

					for (Object o : ListJS.orSelf(args[0])) {
						eventHandler.listen(scriptType, String.valueOf(Context.jsToJava(o, String.class)), handler);
					}
				}
			}

			return null;
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
		var keyString = String.valueOf(key);
		EventHandlerWrapper wrapper = handlers.get(keyString);

		if (wrapper == null) {
			var handler = group.getHandlers().get(keyString);

			if (handler == null) {
				scriptType.console.pushLineNumber();
				scriptType.console.error("Unknown event '" + keyString + "'!");
				scriptType.console.popLineNumber();
				return EventHandlerWrapper.NONE;
			} else {
				wrapper = new EventHandlerWrapper(scriptType, handler);
				handlers.put(keyString, wrapper);
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

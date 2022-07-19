package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.BaseFunction;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EventGroupWrapper extends HashMap<String, BaseFunction> {
	private final ScriptType scriptType;
	private final EventGroup group;
	private final Map<String, EventHandler> handlers;

	public EventGroupWrapper(ScriptType scriptType, EventGroup group) {
		this.scriptType = scriptType;
		this.group = group;
		this.handlers = new HashMap<>();
	}

	@Override
	public BaseFunction get(Object key) {
		var keyString = String.valueOf(key);
		var handler = handlers.get(keyString);

		if (handler == null) {
			handler = group.getHandlers().get(keyString);

			if (handler == null) {
				scriptType.console.pushLineNumber();
				scriptType.console.error("Unknown event '" + keyString + "'!");
				scriptType.console.popLineNumber();
				return new BaseFunction();
			} else {
				handlers.put(keyString, handler);
			}
		}

		return handler;
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

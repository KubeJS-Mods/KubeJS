package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.BaseFunction;

import java.util.HashMap;
import java.util.Set;

public class EventGroupWrapper extends HashMap<String, BaseFunction> {
	private final ScriptType scriptType;
	private final EventGroup group;

	public EventGroupWrapper(ScriptType scriptType, EventGroup group) {
		this.scriptType = scriptType;
		this.group = group;
	}

	@Override
	public BaseFunction get(Object key) {
		var keyString = String.valueOf(key);
		var handler = group.getHandlers().get(keyString);

		if (handler == null) {
			scriptType.console.error("Unknown event '%s.%s'!".formatted(group.name, keyString));
			return new BaseFunction();
		}

		return handler;
	}

	@Override
	public boolean containsKey(Object key) {
		return true;
	}

	@Override
	public Set<String> keySet() {
		return group.getHandlers().keySet();
	}
}

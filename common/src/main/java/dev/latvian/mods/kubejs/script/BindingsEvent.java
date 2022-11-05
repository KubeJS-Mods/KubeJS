package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.rhino.SharedContextData;

/**
 * @author LatvianModder
 */
public class BindingsEvent {
	public final ScriptManager manager;
	public final ScriptType type;
	public final SharedContextData contextData;

	public BindingsEvent(ScriptManager m, SharedContextData d) {
		manager = m;
		type = manager.scriptType;
		contextData = d;
	}

	public ScriptType getType() {
		return type;
	}

	public void add(String name, Object value) {
		if (value != null) {
			contextData.addToTopLevelScope(name, value);
		}
	}
}
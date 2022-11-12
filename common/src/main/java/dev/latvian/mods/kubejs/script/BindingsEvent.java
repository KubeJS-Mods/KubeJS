package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.rhino.Scriptable;

/**
 * @author LatvianModder
 */
public class BindingsEvent {
	public final ScriptManager manager;
	public final Scriptable scope;

	public BindingsEvent(ScriptManager m, Scriptable s) {
		manager = m;
		scope = s;
	}

	public ScriptType getType() {
		return manager.scriptType;
	}

	public void add(String name, Object value) {
		if (value != null) {
			manager.context.addToScope(scope, name, value);
		}
	}
}
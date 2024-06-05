package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.rhino.Scriptable;

public record BindingRegistry(KubeJSContext context, Scriptable scope) {
	public ScriptType type() {
		return context.getType();
	}

	public void add(String name, Object value) {
		if (value != null) {
			context.addToScope(scope, name, value);
		}
	}
}
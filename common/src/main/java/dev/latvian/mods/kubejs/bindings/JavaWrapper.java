package dev.latvian.mods.kubejs.bindings;

import dev.latvian.mods.kubejs.script.ScriptManager;

public class JavaWrapper {
	private final ScriptManager manager;

	public JavaWrapper(ScriptManager manager) {
		this.manager = manager;
	}

	public Object loadClass(String className) {
		return manager.loadJavaClass(className, true);
	}
}

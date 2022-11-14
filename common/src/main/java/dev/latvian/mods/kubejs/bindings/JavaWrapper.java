package dev.latvian.mods.kubejs.bindings;

import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import org.slf4j.LoggerFactory;

public class JavaWrapper {
	private final ScriptManager manager;

	public JavaWrapper(ScriptManager manager) {
		this.manager = manager;
	}

	public Object loadClass(String className) {
		return manager.loadJavaClass(className, true);
	}

	public ConsoleJS createConsole(String name) {
		return new ConsoleJS(manager.scriptType, LoggerFactory.getLogger(name));
	}
}

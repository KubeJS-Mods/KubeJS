package dev.latvian.mods.kubejs.bindings;

import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import org.slf4j.LoggerFactory;

@Info("Methods for working with Java classes. Reflection my beloved ♥")
public class JavaWrapper {
	private final ScriptManager manager;

	public JavaWrapper(ScriptManager manager) {
		this.manager = manager;
	}

	@Info("""
		Loads the specified class.
		The returned object can have public static methods and fields accessed directly from it.
		Constructors can be used with the new keyword.
		 """)
	public Object loadClass(String className) {
		return manager.loadJavaClass(className, true);
	}

	@Info("Creates a custom ConsoleJS instance for you to use to, well, log stuff")
	public ConsoleJS createConsole(String name) {
		return new ConsoleJS(manager.scriptType, LoggerFactory.getLogger(name));
	}
}

package dev.latvian.mods.kubejs.bindings;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.StringWithContext;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

@Info("Methods for working with Java classes. Reflection my beloved ♥")
public interface JavaWrapper {
	@Info("""
		Loads the specified class, and throws error if class it not found or allowed.
		The returned object can have public static methods and fields accessed directly from it.
		Constructors can be used with the new keyword.
		""")
	static Object loadClass(StringWithContext className) {
		return className.cx().loadJavaClass(className.string(), true);
	}

	@Info("""
		Loads the specified class, and returns null if class is not found or allowed.
		The returned object can have public static methods and fields accessed directly from it.
		Constructors can be used with the new keyword.
		""")
	@Nullable
	static Object tryLoadClass(StringWithContext className) {
		return className.cx().loadJavaClass(className.string(), false);
	}

	@Info("Creates a custom ConsoleJS instance for you to use to, well, log stuff")
	static ConsoleJS createConsole(StringWithContext name) {
		return new ConsoleJS(name.cx().getType(), LoggerFactory.getLogger(name.string()));
	}
}

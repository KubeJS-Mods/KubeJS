package dev.latvian.mods.kubejs.plugin.builtin.wrapper;

import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.RecordTypeInfo;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.HideFromJS;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Info("Methods for working with Java classes. Reflection my beloved ♥")
public interface JavaWrapper {
	@Info("""
		Loads the specified class, and throws error if class it not found or allowed.
		The returned object can have public static methods and fields accessed directly from it.
		Constructors can be used with the new keyword.
		""")
	static Object loadClass(KubeJSContext cx, String className) {
		return cx.loadJavaClass(className, true);
	}

	@Info("""
		Loads the specified class, and returns null if class is not found or allowed.
		The returned object can have public static methods and fields accessed directly from it.
		Constructors can be used with the new keyword.
		""")
	@Nullable
	static Object tryLoadClass(KubeJSContext cx, String className) {
		return cx.loadJavaClass(className, false);
	}

	@Info("Creates a custom ConsoleJS instance for you to use to, well, log stuff")
	static ConsoleJS createConsole(KubeJSContext cx, String name) {
		return new ConsoleJS(cx.getType(), LoggerFactory.getLogger(name));
	}

	static <T> T makeFunctionProxy(Context cx, TypeInfo targetClass, BaseFunction function) {
		return Cast.to(cx.createInterfaceAdapter(targetClass, function));
	}

	@Info("Cast the object to a target type, use if Rhino can't determine the parameter type due to type erasure.")
	static <T> T cast(Context cx, Class<T> targetClass, Object object) {
		return Cast.to(cx.jsToJava(object, TypeInfo.of(targetClass)));
	}

	@Nullable
	@HideFromJS
	static Class<?> tryLoadClass(String className) {
		try {
			return Class.forName(className);
		} catch (Exception ignored) {
			return null;
		}
	}

	static <R extends Record> R mergeRecord(Context cx, R original, Map<String, ?> merge) {
		var typeInfo = (RecordTypeInfo) TypeInfo.of(original.getClass());

		return original;
	}
}

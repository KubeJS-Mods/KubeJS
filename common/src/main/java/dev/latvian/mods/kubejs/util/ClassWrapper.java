package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaClass;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.util.CustomJavaToJsWrapper;

public record ClassWrapper<T>(Class<T> wrappedClass) implements CustomJavaToJsWrapper {
	@Override
	public Scriptable convertJavaToJs(Context cx, Scriptable scope, Class<?> staticType) {
		return new NativeJavaClass(scope, wrappedClass);
	}

	@Override
	public String toString() {
		return "ClassWrapper[" + wrappedClass.getName() + "]";
	}
}
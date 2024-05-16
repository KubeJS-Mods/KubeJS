package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.rhino.util.CustomJavaToJsWrapperProvider;

import java.util.function.Predicate;

public record CustomJavaToJsWrappersEvent(ScriptType type, KubeJSContextFactory contextFactory) {
	public <T> void add(Class<T> type, CustomJavaToJsWrapperProvider<T> provider) {
		contextFactory.addCustomJavaToJsWrapper(type, provider);
	}

	public <T> void add(Predicate<T> predicate, CustomJavaToJsWrapperProvider<T> provider) {
		contextFactory.addCustomJavaToJsWrapper(predicate, provider);
	}
}
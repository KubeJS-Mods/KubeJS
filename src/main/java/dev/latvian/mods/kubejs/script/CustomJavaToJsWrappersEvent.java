package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.rhino.util.CustomJavaToJsWrapperProvider;

import java.util.function.Predicate;

public class CustomJavaToJsWrappersEvent {
	public final ScriptManager manager;

	public CustomJavaToJsWrappersEvent(ScriptManager m) {
		manager = m;
	}

	public ScriptType getScriptType() {
		return manager.scriptType;
	}

	public <T> void add(Class<T> type, CustomJavaToJsWrapperProvider<T> provider) {
		manager.context.addCustomJavaToJsWrapper(type, provider);
	}

	public <T> void add(Predicate<T> predicate, CustomJavaToJsWrapperProvider<T> provider) {
		manager.context.addCustomJavaToJsWrapper(predicate, provider);
	}
}
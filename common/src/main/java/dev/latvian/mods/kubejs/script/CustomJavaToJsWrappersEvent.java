package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.rhino.SharedContextData;
import dev.latvian.mods.rhino.util.CustomJavaToJsWrapperProvider;

import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class CustomJavaToJsWrappersEvent {
	public final ScriptManager manager;
	public final ScriptType scriptType;
	public final SharedContextData data;

	public CustomJavaToJsWrappersEvent(ScriptManager m, SharedContextData d) {
		manager = m;
		scriptType = manager.type;
		data = d;
	}

	public ScriptType getScriptType() {
		return scriptType;
	}

	public <T> void add(Class<T> type, CustomJavaToJsWrapperProvider<T> provider) {
		data.addCustomJavaToJsWrapper(type, provider);
	}

	public <T> void add(Predicate<T> predicate, CustomJavaToJsWrapperProvider<T> provider) {
		data.addCustomJavaToJsWrapper(predicate, provider);
	}
}
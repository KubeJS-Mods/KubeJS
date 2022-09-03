package dev.latvian.mods.kubejs.script;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.latvian.mods.rhino.SharedContextData;
import dev.latvian.mods.rhino.util.CustomJavaToJsWrapperProvider;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class CustomJavaToJsWrappersEvent {
	public static final Event<Consumer<CustomJavaToJsWrappersEvent>> EVENT = EventFactory.createConsumerLoop(CustomJavaToJsWrappersEvent.class);
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
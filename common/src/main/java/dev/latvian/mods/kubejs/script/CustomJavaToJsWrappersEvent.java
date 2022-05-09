package dev.latvian.mods.kubejs.script;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.latvian.mods.rhino.Context;
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
	public final Context context;

	public CustomJavaToJsWrappersEvent(ScriptManager m, Context cx) {
		manager = m;
		scriptType = manager.type;
		context = cx;
	}

	public ScriptType getScriptType() {
		return scriptType;
	}

	public <T> void add(Class<T> type, CustomJavaToJsWrapperProvider<T> provider) {
		context.addCustomJavaToJsWrapper(type, provider);
	}

	public <T> void add(Predicate<T> predicate, CustomJavaToJsWrapperProvider<T> provider) {
		context.addCustomJavaToJsWrapper(predicate, provider);
	}
}
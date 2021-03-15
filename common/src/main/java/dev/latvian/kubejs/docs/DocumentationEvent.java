package dev.latvian.kubejs.docs;

import me.shedaniel.architectury.event.Event;
import me.shedaniel.architectury.event.EventFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class DocumentationEvent {
	public static final Event<Consumer<DocumentationEvent>> EVENT = EventFactory.createConsumerLoop(DocumentationEvent.class);

	public static Map<Class<?>, TypeDefinition> collectDocs() {
		Map<Class<?>, TypeDefinition> map = new HashMap<>();
		EVENT.invoker().accept(new DocumentationEvent(map));
		return map;
	}

	private final Map<Class<?>, TypeDefinition> types;

	private DocumentationEvent(Map<Class<?>, TypeDefinition> m) {
		types = m;
	}

	public void add(Class<?> c) {
		TypeDefinition def = type(c);
	}

	public TypeDefinition type(Class<?> c) {
		return types.computeIfAbsent(c, c1 -> new TypeDefinition(this, c1));
	}

	public void createFile() {
	}
}
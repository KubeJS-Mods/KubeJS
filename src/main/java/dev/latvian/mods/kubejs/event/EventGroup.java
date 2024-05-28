package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptTypePredicate;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class EventGroup {
	public static EventGroup of(String name) {
		return new EventGroup(name);
	}

	public final String name;
	private final Map<String, EventHandler> handlers;

	private EventGroup(String n) {
		name = n;
		handlers = new HashMap<>();
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof EventGroup g && name.equals(g.name);
	}

	public EventHandler add(String name, ScriptTypePredicate scriptType, Supplier<Class<? extends KubeEvent>> eventType) {
		var handler = new EventHandler(this, name, scriptType, eventType);
		handlers.put(name, handler);
		return handler;
	}

	public EventHandler startup(String name, Supplier<Class<? extends KubeEvent>> eventType) {
		return add(name, ScriptType.STARTUP, eventType);
	}

	public EventHandler server(String name, Supplier<Class<? extends KubeEvent>> eventType) {
		return add(name, ScriptType.SERVER, eventType);
	}

	public EventHandler client(String name, Supplier<Class<? extends KubeEvent>> eventType) {
		return add(name, ScriptType.CLIENT, eventType);
	}

	public EventHandler common(String name, Supplier<Class<? extends KubeEvent>> eventType) {
		return add(name, ScriptTypePredicate.COMMON, eventType);
	}

	public <T> SpecializedEventHandler<T> add(String name, ScriptTypePredicate scriptType, Extra<T> extra, Supplier<Class<? extends KubeEvent>> eventType) {
		var handler = new SpecializedEventHandler<>(this, name, scriptType, extra, eventType);
		handlers.put(name, handler);
		return handler;
	}

	public <T> SpecializedEventHandler<T> startup(String name, Extra<T> extra, Supplier<Class<? extends KubeEvent>> eventType) {
		return add(name, ScriptType.STARTUP, extra, eventType);
	}

	public <T> SpecializedEventHandler<T> server(String name, Extra<T> extra, Supplier<Class<? extends KubeEvent>> eventType) {
		return add(name, ScriptType.SERVER, extra, eventType);
	}

	public <T> SpecializedEventHandler<T> client(String name, Extra<T> extra, Supplier<Class<? extends KubeEvent>> eventType) {
		return add(name, ScriptType.CLIENT, extra, eventType);
	}

	public <T> SpecializedEventHandler<T> common(String name, Extra<T> extra, Supplier<Class<? extends KubeEvent>> eventType) {
		return add(name, ScriptTypePredicate.COMMON, extra, eventType);
	}

	public Map<String, EventHandler> getHandlers() {
		return handlers;
	}
}

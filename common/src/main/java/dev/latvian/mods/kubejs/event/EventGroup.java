package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.BaseFunction;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class EventGroup extends HashMap<String, BaseFunction> {
	private static final Map<String, EventGroup> MAP = new HashMap<>();

	public static Map<String, EventGroup> getGroups() {
		return Collections.unmodifiableMap(MAP);
	}

	public static EventGroup of(String name) {
		return new EventGroup(name);
	}

	public final String name;
	private final Map<String, EventHandler> handlers;

	private EventGroup(String n) {
		name = n;
		handlers = new HashMap<>();
	}

	public void register() {
		MAP.put(name, this);
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

	public EventHandler add(String name, ScriptType scriptType, Supplier<Class<? extends EventJS>> eventType) {
		EventHandler handler = new EventHandler(this, name, scriptType, eventType);
		handlers.put(name, handler);
		return handler;
	}

	public EventHandler startup(String name, Supplier<Class<? extends EventJS>> eventType) {
		return add(name, ScriptType.STARTUP, eventType);
	}

	public EventHandler server(String name, Supplier<Class<? extends EventJS>> eventType) {
		return add(name, ScriptType.SERVER, eventType);
	}

	public EventHandler client(String name, Supplier<Class<? extends EventJS>> eventType) {
		return add(name, ScriptType.CLIENT, eventType);
	}

	public Map<String, EventHandler> getHandlers() {
		return handlers;
	}
}

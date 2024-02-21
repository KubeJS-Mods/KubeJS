package dev.latvian.mods.kubejs.neoforge;

import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Function;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;

import java.util.ArrayList;
import java.util.List;

public class NativeEventHandler extends BaseFunction {
	private final List<NativeEventConsumer> consumers = new ArrayList<>();
	private final String name;
	private final Class<? extends Event> eventClass;

	public NativeEventHandler(String name, Class<? extends Event> eventClass) {
		this.name = name;
		this.eventClass = eventClass;
	}

	@HideFromJS
	public void unregister() {
		for (NativeEventConsumer consumer : consumers) {
			NeoForge.EVENT_BUS.unregister(consumer);
		}
	}

	@Override
	public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
		try {
			if (args.length < 1 || args.length > 2) {
				throw new IllegalStateException("Expected 1 or 2 arguments for native events. Event syntax: " + getExampleSyntax());
			}

			Object unknownPriority = args.length == 2 ? args[0] : EventPriority.NORMAL;
			Object unknownFunction = args.length == 2 ? args[1] : args[0];
			if (!(unknownFunction instanceof Function)) {
				throw new IllegalStateException("Expected argument to be a function. Event syntax: " + getExampleSyntax());
			}

			var priority = (EventPriority) Context.jsToJava(cx, unknownPriority, EventPriority.class);
			var consumer = (NativeEventConsumer) Context.jsToJava(cx, unknownFunction, NativeEventConsumer.class);
			var securedConsumer = secure(consumer);
			//noinspection unchecked
			NeoForge.EVENT_BUS.addListener(priority, false, (Class<Event>) eventClass, securedConsumer);
			consumers.add(securedConsumer);
		} catch (Exception ex) {
			ScriptType.STARTUP.console.error(ex.getLocalizedMessage());
		}

		return null;
	}

	private String getExampleSyntax() {
		String def = NativeEvents.NAME + "." + name + "(event => { ... })";
		String priority = NativeEvents.NAME + "." + name + "(priority, event => { ... })";
		return def + " or " + priority;
	}

	private NativeEventConsumer secure(NativeEventConsumer consumer) {
		return event -> {
			try {
				consumer.accept(event);
			} catch (Exception ex) {
				NativeEvents.throwException("Error in native event '" + NativeEvents.NAME + "." + name + "'", ex);
			}
		};
	}
}

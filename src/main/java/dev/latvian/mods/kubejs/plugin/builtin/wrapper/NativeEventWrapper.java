package dev.latvian.mods.kubejs.plugin.builtin.wrapper;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Consumer;

public interface NativeEventWrapper {
	@HideFromJS
	record Listeners(Collection<Consumer<Event>> listeners) implements Consumer<Event> {
		public record Key(Class<?> eventClass, EventPriority priority) {
			@Override
			public boolean equals(Object o) {
				return o == this || o instanceof Key(Class<?> aClass, EventPriority priority1) && aClass == eventClass && priority1 == priority;
			}
		}

		@Override
		public void accept(Event event) {
			for (var listener : listeners) {
				listener.accept(event);
			}
		}
	}

	static void onEvent(Context cx, Class<?> eventClass, Consumer<Event> consumer) {
		onEvent(cx, EventPriority.NORMAL, eventClass, consumer);
	}

	static void onEvent(Context cx, EventPriority priority, Class<?> eventClass, Consumer<Event> consumer) {
		if (!Event.class.isAssignableFrom(eventClass)) {
			throw new IllegalArgumentException("Event class must extend net.neoforged.bus.api.Event!");
		}

		var scriptType = ((KubeJSContext) cx).kjsFactory.manager.scriptType;
		var key = new Listeners.Key(eventClass, priority == null ? EventPriority.NORMAL : priority);

		var listeners = scriptType.nativeEventListeners.get(key);

		if (listeners == null) {
			listeners = new Listeners(new LinkedList<>());
			scriptType.nativeEventListeners.put(key, listeners);
			var bus = IModBusEvent.class.isAssignableFrom(eventClass) ? KubeJS.modEventBus : NeoForge.EVENT_BUS;
			bus.addListener(priority, false, (Class) eventClass, listeners);
		}

		listeners.listeners.add(consumer);
	}
}

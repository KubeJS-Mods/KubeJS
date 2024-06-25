package dev.latvian.mods.kubejs.neoforge;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.rhino.Context;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.function.Consumer;

public interface NativeEventWrapper {
	static void onEvent(Context cx, Class<?> eventClass, Consumer<Event> consumer) {
		onEvent(cx, EventPriority.NORMAL, eventClass, consumer);
	}

	static void onEvent(Context cx, EventPriority priority, Class<?> eventClass, Consumer<Event> consumer) {
		if (!Event.class.isAssignableFrom(eventClass)) {
			throw new IllegalArgumentException("Event class must extend net.neoforged.bus.api.Event!");
		}

		var scriptType = ((KubeJSContext) cx).kjsFactory.manager.scriptType;
		var key = new NativeEventListeners.Key(eventClass, priority == null ? EventPriority.NORMAL : priority);

		var listeners = scriptType.nativeEventListeners.get(key);

		if (listeners == null) {
			listeners = new NativeEventListeners();
			scriptType.nativeEventListeners.put(key, listeners);

			IEventBus bus;

			if (IModBusEvent.class.isAssignableFrom(eventClass)) {
				bus = KubeJS.modEventBus;
			} else {
				bus = NeoForge.EVENT_BUS;
			}

			bus.addListener(priority, false, (Class) eventClass, listeners);
		}

		listeners.listeners.add(consumer);
	}
}

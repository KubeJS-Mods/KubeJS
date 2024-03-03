package dev.latvian.mods.kubejs.neoforge;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaObject;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.util.CustomJavaToJsWrapper;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NativeEvents implements CustomJavaToJsWrapper {

	public static final String NAME = "NativeEvents";
	@Nullable
	private static NativeEvents lastKnownBus;
	private final Map<String, NativeEventHandler> handlers = new HashMap<>();
	private final List<NativeEventConsumer> directConsumers = new ArrayList<>();

	public static NativeEvents create() {
		if (lastKnownBus != null) {
			lastKnownBus.clear();
		}

		NativeEvents bus = new NativeEvents();
		lastKnownBus = bus;
		return bus;
	}

	private void clear() {
		ScriptType.STARTUP.console.info("Clearing native events");
		for (var handler : handlers.values()) {
			handler.unregister();
		}
		handlers.clear();

		for (var consumer : directConsumers) {
			NeoForge.EVENT_BUS.unregister(consumer);
		}
		directConsumers.clear();
	}

	private NativeEvents() {
	}

	@Override
	public Scriptable convertJavaToJs(Context context, Scriptable scriptable, Class<?> aClass) {
		return new Wrapper(scriptable, this, aClass, context);
	}

	@Nullable
	public NativeEventHandler getHandler(String name) {
		var handler = handlers.get(name);
		if (handler != null) {
			return handler;
		}

		var eventClass = NeoForgeEventsLookup.INSTANCE.get(name);
		if (eventClass == null) {
			return null;
		}

		handler = new NativeEventHandler(name, eventClass);
		handlers.put(name, handler);
		return handler;
	}

	public void printAllEvents() {
		NeoForgeEventsLookup.INSTANCE.getEvents()
			.entrySet()
			.stream()
			.sorted(Comparator.comparing(Map.Entry::getKey))
			.forEach(entry -> KubeJS.LOGGER.info(entry.getKey() + " - ['" + entry.getValue().getName() + "']"));
	}

	public Object onEvent(Object eventClass, NativeEventConsumer consumer) {
		return onEvent(EventPriority.NORMAL, eventClass, consumer);
	}

	public Object onEvent(EventPriority priority, Object eventClass, NativeEventConsumer consumer) {
		if (!(eventClass instanceof CharSequence || eventClass instanceof Class)) {
			throw new RuntimeException("Invalid syntax! " + NAME + ".onEvent(eventType, function) requires event class and handler");
		}

		try {
			var type = eventClass instanceof Class<?> c ? c : Class.forName(eventClass.toString());
			var secured = secure(eventClass, consumer);
			//noinspection unchecked
			NeoForge.EVENT_BUS.addListener(priority, false, (Class<Event>) type, secured);
			directConsumers.add(secured);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		return null;
	}

	private NativeEventConsumer secure(Object eventClass, NativeEventConsumer consumer) {
		return event -> {
			try {
				consumer.accept(event);
			} catch (Exception ex) {
				throwException("Error in native event when using 'onEvent' for " + eventClass, ex);
			}
		};
	}

	/**
	 * Helper method to throw and log an exception with the stack trace. Rhino seems to not be able to print the full stacktrace.
	 * The user still only gets the basic message inside `startup.log`. The full stacktrace will be logged at `latest.log`
	 *
	 * @param msg The message
	 * @param tx  The exception
	 */
	public static void throwException(String msg, Throwable tx) {
		ScriptType.STARTUP.console.error(msg + ": " + tx.getLocalizedMessage());
		for (var ste : tx.getStackTrace()) {
			KubeJS.LOGGER.error(ste.toString());
		}
	}

	public static class Wrapper extends NativeJavaObject {
		private final NativeEvents nativeEvents;
		private final BaseFunction empty = new BaseFunction() {
			@Override
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				return null;
			}
		};

		public Wrapper(Scriptable scope, NativeEvents nativeEvents, Class<?> staticType, Context cx) {
			super(scope, nativeEvents, staticType, cx);
			this.nativeEvents = nativeEvents;
		}

		@Override
		public Object get(Context cx, String name, Scriptable start) {
			NativeEventHandler handler = nativeEvents.getHandler(name);
			if (handler != null) {
				return handler;
			}

			Object result = super.get(cx, name, start);
			if (result == null || result == Scriptable.NOT_FOUND) {
				return empty;
			}

			return result;
		}
	}
}

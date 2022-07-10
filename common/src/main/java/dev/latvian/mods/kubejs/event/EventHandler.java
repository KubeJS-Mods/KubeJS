package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.RhinoException;
import dev.latvian.mods.rhino.util.HideFromJS;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Example
 * <p>
 * <code>public static final EventHandler EVENT = EventHandler.of(ScriptType.SERVER, ItemRightClickEventJS.class).cancellable();</code>
 */
public class EventHandler {
	public static EventHandler of(ScriptType scriptType, Class<? extends EventJS> eventType) {
		return new EventHandler(null, scriptType, eventType);
	}

	public static EventHandler startup(Class<? extends EventJS> eventType) {
		return of(ScriptType.STARTUP, eventType);
	}

	public static EventHandler server(Class<? extends EventJS> eventType) {
		return of(ScriptType.SERVER, eventType);
	}

	public static EventHandler client(Class<? extends EventJS> eventType) {
		return of(ScriptType.CLIENT, eventType);
	}

	public final EventHandler parent;
	public final ScriptType scriptType;
	public final Class<? extends EventJS> eventType;
	public boolean cancelable;
	public String eventName;
	private IEventHandler[] handlers;
	public final Set<String> legacyEventIds;

	private EventHandler(@Nullable EventHandler p, ScriptType st, Class<? extends EventJS> e) {
		parent = p;
		scriptType = st;
		eventType = e;
		cancelable = false;
		eventName = eventType.getSimpleName().replace("EventJS", "");

		if (eventName.isEmpty()) {
			throw new IllegalArgumentException("Event name is empty! Use .name(string) to override it");
		}

		eventName = eventName.substring(0, 1).toLowerCase() + eventName.substring(1);
		handlers = null;
		legacyEventIds = new HashSet<>();
	}

	/**
	 * Allow event.cancel() to be called
	 */
	@HideFromJS
	public EventHandler cancelable() {
		cancelable = true;
		return this;
	}

	public boolean isCancelable() {
		return cancelable;
	}

	/**
	 * Override name of event handler, defaults to simplified version of class name with EventJS removed
	 * <p>
	 * E.g. ItemRightClickEventJS -> ItemRightClick
	 */
	@HideFromJS
	public EventHandler name(String name) {
		eventName = name;
		return this;
	}

	@HideFromJS
	public EventHandler legacy(String eventId) {
		legacyEventIds.add(eventId);
		return this;
	}

	@HideFromJS
	public void clear() {
		handlers = null;
	}

	/**
	 * Call this inside {@link KubeJSPlugin#registerEvents()}
	 */
	@HideFromJS
	public void register() {
		scriptType.eventHandlers.put(eventName, this);

		for (String s : legacyEventIds) {
			scriptType.legacyEventHandlers.put(s, this);
		}
	}

	// x.listen(event => {}) syntax
	public void listen(IEventHandler handler) {
		if (handlers == null) {
			handlers = new IEventHandler[]{handler};
		} else {
			ArrayList<IEventHandler> list = new ArrayList<>(handlers.length + 1);
			list.addAll(Arrays.asList(handlers));
			list.add(handler);
			handlers = list.toArray(new IEventHandler[0]);
		}
	}

	// x.listener = event => {} syntax
	public void setListener(IEventHandler handler) {
		listen(handler);
	}

	@HideFromJS
	public boolean post(EventJS event) {
		if (handlers == null || handlers.length == 0) {
			event.afterPosted(false);
			return false;
		}

		boolean c = isCancelable() || event.canCancel();

		for (var handler : handlers) {
			try {
				handler.onEvent(event);

				if (c && event.isCanceled()) {
					event.afterPosted(true);
					return true;
				}
			} catch (RhinoException ex) {
				scriptType.console.error("Error occurred while handling event '" + eventName + "': " + ex.getMessage());
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}

		event.afterPosted(false);
		return false;
	}
}

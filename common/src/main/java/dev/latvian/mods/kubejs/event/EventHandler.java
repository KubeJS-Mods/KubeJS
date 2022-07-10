package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.RhinoException;
import dev.latvian.mods.rhino.util.HideFromJS;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Example
 * <p>
 * <code>public static final EventHandler EVENT = EventHandler.of(ScriptType.SERVER, ItemRightClickEventJS.class).cancellable();</code>
 */
public final class EventHandler {
	public static EventHandler of(ScriptType scriptType, Class<? extends EventJS> eventType) {
		return new EventHandler(null, "", scriptType, eventType);
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
	public final String subId;
	public final ScriptType scriptType;
	public final Class<? extends EventJS> eventType;
	private boolean cancelable;
	private String name;
	private IEventHandler[] handlers;
	private Map<String, EventHandler> subHandlers;
	private final Set<String> legacyEventIds;

	private EventHandler(@Nullable EventHandler p, String sid, ScriptType st, Class<? extends EventJS> e) {
		parent = p;
		subId = sid;
		scriptType = st;
		eventType = e;
		cancelable = false;
		name = eventType.getSimpleName().replace("EventJS", "");

		if (!name.isEmpty()) {
			name = name.substring(0, 1).toLowerCase() + name.substring(1);
		}

		handlers = null;
		subHandlers = null;
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
	public EventHandler name(String n) {
		name = n;
		return this;
	}

	public String getName() {
		return name;
	}

	@HideFromJS
	public EventHandler legacy(String eventId) {
		legacyEventIds.add(eventId);
		return this;
	}

	@HideFromJS
	public void clear() {
		handlers = null;
		subHandlers = null;
	}

	/**
	 * Call this inside {@link KubeJSPlugin#registerEvents()} or {@link KubeJSPlugin#registerClientEvents()}
	 */
	@HideFromJS
	public void register() {
		if (name.isBlank()) {
			throw new IllegalArgumentException("Event name is empty! Use .name(string) to override it");
		}

		scriptType.eventHandlers.put(name, this);

		for (String s : legacyEventIds) {
			scriptType.legacyEventHandlers.put(s, this);
		}

		KubeJS.LOGGER.info("Registered '" + eventType.getName() + "' event handler '" + name + "' for " + scriptType.name + " scripts" + (legacyEventIds.isEmpty() ? "" : (" with legacy IDs " + legacyEventIds.stream().map(s -> "'" + s + "'").collect(Collectors.joining(", ", "[", "]")))));
	}

	public EventHandler of(String sub) {
		if (sub.isEmpty()) {
			return this;
		}

		if (parent != null) {
			throw new IllegalStateException("Nested EventHandler.of(id) calls are not supported!");
		}

		if (subHandlers == null) {
			subHandlers = new HashMap<>();
		}

		var h = subHandlers.get(sub);

		if (h == null) {
			h = new EventHandler(this, sub, scriptType, eventType);
			h.cancelable = cancelable;
			h.name = name;
			subHandlers.put(sub, h);
		}

		return h;
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

	/***
	 * @return true if event was canceled
	 */
	@HideFromJS
	public boolean post(EventJS event, String sub) {
		if (parent != null) {
			return parent.post(event, sub);
		}

		boolean b = false;

		var subHandler = sub.isEmpty() || subHandlers == null ? null : subHandlers.get(sub);

		if (subHandler != null) {
			b = subHandler.postToHandlers(event);
		}

		if (!b) {
			b = postToHandlers(event);
		}

		event.afterPosted(b);
		return b;
	}

	private boolean postToHandlers(EventJS event) {
		if (handlers == null || handlers.length == 0) {
			return false;
		}

		boolean c = isCancelable() || event.canCancel();

		for (var handler : handlers) {
			try {
				handler.onEvent(event);

				if (c && event.isCanceled()) {
					return true;
				}
			} catch (RhinoException ex) {
				scriptType.console.error("Error occurred while handling event '" + name + "': " + ex.getMessage());
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}

		return false;
	}

	@HideFromJS
	public boolean post(EventJS event) {
		return post(event, "");
	}
}

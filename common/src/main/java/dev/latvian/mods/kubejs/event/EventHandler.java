package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.RhinoException;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Example
 * <p>
 * <code>public static final EventHandler EVENT = EventHandler.of(ScriptType.SERVER, ItemRightClickEventJS.class).cancellable();</code>
 */
public final class EventHandler {
	public record Container(ScriptType type, IEventHandler handler) {
	}

	public final EventGroup group;
	public final String name;
	public final EventHandler parent;
	public final ScriptType scriptType;
	public final Supplier<Class<? extends EventJS>> eventType;
	private boolean cancelable;
	private List<Container> eventContainers;
	private Map<String, EventHandler> subHandlers;
	private Set<String> legacyEventIds;

	EventHandler(EventGroup g, String n, ScriptType st, Supplier<Class<? extends EventJS>> e, @Nullable EventHandler p) {
		group = g;
		name = n;
		parent = p;
		scriptType = st;
		eventType = e;
		cancelable = false;
		eventContainers = null;
		subHandlers = null;
		legacyEventIds = null;
	}

	/**
	 * Allow event.cancel() to be called
	 */
	public EventHandler cancelable() {
		cancelable = true;
		return this;
	}

	public boolean isCancelable() {
		return cancelable;
	}

	public EventHandler legacy(String eventId) {
		if (legacyEventIds == null) {
			legacyEventIds = new HashSet<>(1);
		}

		legacyEventIds.add(eventId);
		return this;
	}

	public Set<String> getLegacyEventIds() {
		return legacyEventIds == null ? Set.of() : legacyEventIds;
	}

	public boolean clear(ScriptType type) {
		if (subHandlers != null) {
			subHandlers.values().removeIf(h -> h.clear(type));

			if (subHandlers.isEmpty()) {
				subHandlers = null;
			}
		}

		if (eventContainers != null) {
			eventContainers.removeIf(container -> container.type == type);

			if (eventContainers.isEmpty()) {
				eventContainers = null;
			}
		}

		return subHandlers == null && eventContainers == null;
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
			h = new EventHandler(group, sub, scriptType, eventType, this);
			h.cancelable = cancelable;
			subHandlers.put(sub, h);
		}

		return h;
	}

	public void listen(ScriptType type, IEventHandler handler) {
		if (eventContainers == null) {
			eventContainers = new ArrayList<>(1);
		}

		eventContainers.add(new Container(type, handler));
	}

	public boolean post(EventJS event) {
		return post(null, event);
	}

	/**
	 * @return true if event was canceled
	 */
	public boolean post(@Nullable Object subId, EventJS event) {
		if (parent != null) {
			throw new IllegalStateException("Can't call EventHandler.post() from sub-handler!");
		}

		boolean b = false;

		var sub = subId == null ? "" : String.valueOf(subId);
		var subHandler = sub.isEmpty() || subHandlers == null ? null : subHandlers.get(sub);

		if (subHandler != null) {
			b = subHandler.postToHandlers(scriptType, event);

			if (!b && scriptType != ScriptType.STARTUP) {
				b = subHandler.postToHandlers(ScriptType.STARTUP, event);
			}
		}

		if (!b) {
			b = postToHandlers(scriptType, event);

			if (!b && scriptType != ScriptType.STARTUP) {
				b = postToHandlers(ScriptType.STARTUP, event);
			}
		}

		event.afterPosted(b);
		return b;
	}

	private boolean postToHandlers(ScriptType type, EventJS event) {
		if (eventContainers == null || eventContainers.isEmpty()) {
			return false;
		}

		boolean c = isCancelable();

		for (var container : eventContainers) {
			try {
				if (container.type == type) {
					container.handler.onEvent(event);

					if (c && event.isCanceled()) {
						return true;
					}
				}
			} catch (RhinoException ex) {
				scriptType.console.error("Error occurred while handling event '" + name + "': " + ex.getMessage());
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}

		return false;
	}

	@Override
	public String toString() {
		return group + "." + name;
	}
}

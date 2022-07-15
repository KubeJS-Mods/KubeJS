package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.RhinoException;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Example
 * <p>
 * <code>public static final EventHandler EVENT = EventHandler.of(ScriptType.SERVER, ItemRightClickEventJS.class).cancellable();</code>
 */
public final class EventHandler {
	public final EventGroup group;
	public final String name;
	public final ScriptType scriptType;
	public final Supplier<Class<? extends EventJS>> eventType;
	private boolean cancelable;
	private EventHandlerContainer[] eventContainers;
	private Map<String, EventHandlerContainer[]> extraEventContainers;
	private Set<String> legacyEventIds;
	private int extraIdType;

	EventHandler(EventGroup g, String n, ScriptType st, Supplier<Class<? extends EventJS>> e) {
		group = g;
		name = n;
		scriptType = st;
		eventType = e;
		cancelable = false;
		eventContainers = null;
		extraEventContainers = null;
		legacyEventIds = null;
		extraIdType = 0;
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

	public EventHandler requiresExtraId() {
		extraIdType = 1;
		return this;
	}

	public boolean getRequiresExtraId() {
		return extraIdType == 1;
	}

	public EventHandler supportsExtraId() {
		extraIdType = 2;
		return this;
	}

	public boolean getSupportsExtraId() {
		return extraIdType != 0;
	}

	public void clear(ScriptType type) {
		if (eventContainers != null) {
			eventContainers[type.ordinal()] = null;

			if (EventHandlerContainer.isEmpty(eventContainers)) {
				eventContainers = null;
			}
		}

		if (extraEventContainers != null) {
			var entries = extraEventContainers.entrySet().iterator();

			while (entries.hasNext()) {
				var entry = entries.next();
				entry.getValue()[type.ordinal()] = null;

				if (EventHandlerContainer.isEmpty(entry.getValue())) {
					entries.remove();
				}
			}

			if (extraEventContainers.isEmpty()) {
				extraEventContainers = null;
			}
		}
	}

	public void listen(ScriptType type, @Nullable String extraId, IEventHandler handler) {
		if (!type.manager.get().canListenEvents) {
			throw new IllegalArgumentException("Event handler '" + this + "' can only be registered during script loading!");
		}

		String extra = extraId == null ? "" : extraId;

		if (getRequiresExtraId() && extra.isEmpty()) {
			throw new IllegalArgumentException("Event handler '" + this + "' requires extra id!");
		}

		if (!getSupportsExtraId() && !extra.isEmpty()) {
			throw new IllegalArgumentException("Event handler '" + this + "' doesn't support extra id!");
		}

		EventHandlerContainer[] map;

		if (extra.isEmpty()) {
			if (eventContainers == null) {
				eventContainers = new EventHandlerContainer[ScriptType.VALUES.length];
			}

			map = eventContainers;
		} else {
			if (extraEventContainers == null) {
				extraEventContainers = new HashMap<>();
			}

			map = extraEventContainers.get(extra);

			//noinspection Java8MapApi
			if (map == null) {
				map = new EventHandlerContainer[ScriptType.VALUES.length];
				extraEventContainers.put(extra, map);
			}
		}

		var index = type.ordinal();

		if (map[index] == null) {
			map[index] = new EventHandlerContainer(handler);
		} else {
			map[index].add(handler);
		}
	}

	public boolean post(EventJS event) {
		return post(null, event);
	}

	/**
	 * @return true if event was canceled
	 */
	public boolean post(@Nullable Object extraId, EventJS event) {
		boolean b = false;

		var extra = extraId == null ? "" : String.valueOf(extraId);

		if (getRequiresExtraId() && extra.isEmpty()) {
			throw new IllegalArgumentException("Event handler '" + this + "' requires extra id!");
		}

		if (!getSupportsExtraId() && !extra.isEmpty()) {
			throw new IllegalArgumentException("Event handler '" + this + "' doesn't support extra id!");
		}

		var extraContainers = extraEventContainers == null ? null : extraEventContainers.get(extra);

		if (extraContainers != null) {
			b = postToHandlers(scriptType, extraContainers, event);

			if (!b && scriptType != ScriptType.STARTUP) {
				b = postToHandlers(ScriptType.STARTUP, extraContainers, event);
			}
		}

		if (!b && eventContainers != null) {
			b = postToHandlers(scriptType, eventContainers, event);

			if (!b && scriptType != ScriptType.STARTUP) {
				b = postToHandlers(ScriptType.STARTUP, eventContainers, event);
			}
		}

		event.afterPosted(b);
		return b;
	}

	private boolean postToHandlers(ScriptType type, EventHandlerContainer[] containers, EventJS event) {
		var handler = containers[type.ordinal()];

		if (handler != null) {
			try {
				return handler.handle(event, isCancelable());
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

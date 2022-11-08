package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.SharedContextData;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.util.HideFromJS;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * <h3>Example</h3>
 * <p><code>public static final EventHandler CLIENT_RIGHT_CLICKED = ItemEvents.GROUP.client("clientRightClicked", () -> ItemClickedEventJS.class).extra(ItemEvents.SUPPORTS_ITEM);</code></p>
 */
public final class EventHandler extends BaseFunction {
	public final EventGroup group;
	public final String name;
	public final ScriptType scriptType;
	public final Supplier<Class<? extends EventJS>> eventType;
	private boolean cancelable;
	public transient Extra extra;
	private EventHandlerContainer[] eventContainers;
	private Map<Object, EventHandlerContainer[]> extraEventContainers;

	EventHandler(EventGroup g, String n, ScriptType st, Supplier<Class<? extends EventJS>> e) {
		group = g;
		name = n;
		scriptType = st;
		eventType = e;
		cancelable = false;
		extra = null;
		eventContainers = null;
		extraEventContainers = null;
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

	@HideFromJS
	public EventHandler extra(Extra extra) {
		this.extra = extra;
		return this;
	}

	@HideFromJS
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

	public void listen(ScriptType type, @Nullable Object extraId, IEventHandler handler) {
		if (!type.manager.get().canListenEvents) {
			throw new IllegalStateException("Event handler '" + this + "' can only be registered during script loading!");
		}

		if (type != scriptType && type != ScriptType.STARTUP) {
			var types = EnumSet.of(scriptType, ScriptType.STARTUP);
			throw new UnsupportedOperationException("Tried to register event handler '" + this + "' for invalid script type " + type + "! Valid script types: " + types);
		}

		if (extraId != null && extra != null) {
			extraId = Wrapper.unwrapped(extraId);
			extraId = extra.transformer.transform(extraId);
		}

		if (extra != null && extra.required && extraId == null) {
			throw new IllegalArgumentException("Event handler '" + this + "' requires extra id!");
		}

		if (extra == null && extraId != null) {
			throw new IllegalArgumentException("Event handler '" + this + "' doesn't support extra id!");
		}

		if (extra != null && extraId != null && !extra.validator.test(extraId)) {
			throw new IllegalArgumentException("Event handler '" + this + "' doesn't accept id '" + extra.toString.transform(extraId) + "'!");
		}

		EventHandlerContainer[] map;

		if (extraId == null) {
			if (eventContainers == null) {
				eventContainers = new EventHandlerContainer[ScriptType.VALUES.length];
			}

			map = eventContainers;
		} else {
			if (extraEventContainers == null) {
				extraEventContainers = extra.identity ? new IdentityHashMap<>() : new HashMap<>();
			}

			map = extraEventContainers.get(extraId);

			//noinspection Java8MapApi
			if (map == null) {
				map = new EventHandlerContainer[ScriptType.VALUES.length];
				extraEventContainers.put(extraId, map);
			}
		}

		var index = type.ordinal();

		if (map[index] == null) {
			map[index] = new EventHandlerContainer(handler);
		} else {
			map[index].add(handler);
		}
	}

	/**
	 * @return true if event was canceled
	 */
	public boolean post(EventJS event) {
		return post(null, event);
	}

	/**
	 * @return true if event was canceled
	 */
	public boolean post(@Nullable Object extraId, EventJS event) {
		return post(extraId, event, false);
	}

	/**
	 * @return true if event was canceled
	 */
	public boolean post(@Nullable Object extraId, EventJS event, boolean onlyPostToExtra) {
		boolean b = false;

		if (extraId != null && extra != null) {
			extraId = Wrapper.unwrapped(extraId);
			extraId = extra.transformer.transform(extraId);
		}

		if (extra != null && extra.required && extraId == null) {
			throw new IllegalArgumentException("Event handler '" + this + "' requires extra id!");
		}

		if (extra == null && extraId != null) {
			throw new IllegalArgumentException("Event handler '" + this + "' doesn't support extra id " + extraId + "!");
		}

		var extraContainers = extraEventContainers == null ? null : extraEventContainers.get(extraId);

		if (extraContainers != null) {
			b = postToHandlers(scriptType, extraContainers, event);

			if (!b && scriptType != ScriptType.STARTUP) {
				b = postToHandlers(ScriptType.STARTUP, extraContainers, event);
			}
		}

		if (!b && eventContainers != null && !onlyPostToExtra) {
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
			return handler.handle(type, this, event, isCancelable());
		}

		return false;
	}

	@Override
	public String toString() {
		return group + "." + name;
	}

	@Override
	public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
		SharedContextData contextData = SharedContextData.get(cx, scope);
		ScriptType type = (ScriptType) contextData.getExtraProperty("Type");

		if (type == null) {
			throw new IllegalStateException("Unknown script type!");
		}

		try {
			if (args.length == 1) {
				listen(type, null, (IEventHandler) Context.jsToJava(contextData, args[0], IEventHandler.class));
			} else if (args.length == 2) {
				var handler = (IEventHandler) Context.jsToJava(contextData, args[1], IEventHandler.class);

				for (Object o : ListJS.orSelf(args[0])) {
					listen(type, o, handler);
				}
			}
		} catch (Exception ex) {
			type.console.error(ex.getLocalizedMessage());
		}

		return null;
	}
}

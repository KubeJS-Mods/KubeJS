package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptTypeHolder;
import dev.latvian.mods.kubejs.script.ScriptTypePredicate;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.rhino.*;
import dev.latvian.mods.rhino.util.HideFromJS;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <h3>Example</h3>
 * <p><code>public static final EventHandler CLIENT_RIGHT_CLICKED = ItemEvents.GROUP.client("clientRightClicked", () -> ItemClickedEventJS.class).extra(ItemEvents.SUPPORTS_ITEM);</code></p>
 */
public final class EventHandler extends BaseFunction {
	public final EventGroup group;
	public final String name;
	public final ScriptTypePredicate scriptTypePredicate;
	public final Supplier<Class<? extends EventJS>> eventType;
	private boolean hasResult;
	public transient Extra extra;
	private EventHandlerContainer[] eventContainers;
	private Map<Object, EventHandlerContainer[]> extraEventContainers;

	EventHandler(EventGroup g, String n, ScriptTypePredicate st, Supplier<Class<? extends EventJS>> e) {
		group = g;
		name = n;
		scriptTypePredicate = st;
		eventType = e;
		hasResult = false;
		extra = null;
		eventContainers = null;
		extraEventContainers = null;
	}

	/**
	 * Allow event.cancel() to be called
	 */
	public EventHandler hasResult() {
		hasResult = true;
		return this;
	}

	public boolean getHasResult() {
		return hasResult;
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

	public boolean hasListeners() {
		return eventContainers != null || extraEventContainers != null;
	}

	/**
	 * Important! extraId won't be transformed for performance reasons. Only use this over {@link EventHandler#hasListeners()} if you think this will be more performant. Recommended only with identity extra IDs.
	 */
	public boolean hasListeners(Object extraId) {
		return eventContainers != null || extraEventContainers != null && extraEventContainers.containsKey(extraId);
	}

	public void listen(ScriptType type, @Nullable Object extraId, IEventHandler handler) {
		if (!type.manager.get().canListenEvents) {
			throw new IllegalStateException("Event handler '" + this + "' can only be registered during script loading!");
		}

		if (!scriptTypePredicate.test(type)) {
			throw new UnsupportedOperationException("Tried to register event handler '" + this + "' for invalid script type " + type + "! Valid script types: " + scriptTypePredicate.getValidTypes());
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

		var line = new int[1];
		var source = Context.getSourcePositionFromStack(type.manager.get().context, line);

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
			map[index] = new EventHandlerContainer(extraId, handler, source, line[0]);
		} else {
			map[index].add(extraId, handler, source, line[0]);
		}
	}

	@HideFromJS
	public void listenJava(ScriptType type, @Nullable Object extraId, IEventHandler handler) {
		var b = type.manager.get().canListenEvents;
		type.manager.get().canListenEvents = true;
		listen(type, extraId, handler);
		type.manager.get().canListenEvents = b;
	}

	/**
	 * @see EventHandler#post(ScriptTypeHolder, Object, EventJS, EventExceptionHandler)
	 */
	public EventResult post(EventJS event) {
		if (scriptTypePredicate instanceof ScriptTypeHolder type) {
			return post(type, null, event);
		} else {
			throw new IllegalStateException("You must specify which script type to post event to");
		}
	}

	/**
	 * @see EventHandler#post(ScriptTypeHolder, Object, EventJS, EventExceptionHandler)
	 */
	public EventResult post(ScriptTypeHolder scriptType, EventJS event) {
		return post(scriptType, null, event);
	}

	/**
	 * @see EventHandler#post(ScriptTypeHolder, Object, EventJS, EventExceptionHandler)
	 */
	// sth, event, exh
	public EventResult post(ScriptTypeHolder scriptType, EventJS event, EventExceptionHandler exh) {
		return post(scriptType, null, event, exh);
	}

	/**
	 * @see EventHandler#post(ScriptTypeHolder, Object, EventJS, EventExceptionHandler)
	 */
	public EventResult post(EventJS event, @Nullable Object extraId) {
		if (scriptTypePredicate instanceof ScriptTypeHolder type) {
			return post(type, extraId, event);
		} else {
			throw new IllegalStateException("You must specify which script type to post event to");
		}
	}

	/**
	 * @see EventHandler#post(ScriptTypeHolder, Object, EventJS, EventExceptionHandler)
	 */
	public EventResult post(EventJS event, @Nullable Object extraId, EventExceptionHandler exh) {
		if (scriptTypePredicate instanceof ScriptTypeHolder type) {
			return post(type, extraId, event, exh);
		} else {
			throw new IllegalStateException("You must specify which script type to post event to");
		}
	}

	/**
	 * @see EventHandler#post(ScriptTypeHolder, Object, EventJS, EventExceptionHandler)
	 */
	public EventResult post(ScriptTypeHolder type, @Nullable Object extraId, EventJS event) {
		return post(type, extraId, event, null);
	}

	/**
	 * @return EventResult that can contain an object. What previously returned true on {@link EventJS#cancel()} now returns {@link EventResult#interruptFalse()}
	 * @see EventJS#cancel()
	 * @see EventJS#success()
	 * @see EventJS#exit()
	 * @see EventJS#cancel(Object)
	 * @see EventJS#success(Object)
	 * @see EventJS#exit(Object)
	 */
	public EventResult post(ScriptTypeHolder type, @Nullable Object extraId, EventJS event, EventExceptionHandler exh) {
		if (!hasListeners()) {
			return EventResult.PASS;
		}

		var scriptType = type.kjs$getScriptType();

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

		var eventResult = EventResult.PASS;

		try {
			var extraContainers = extraEventContainers == null ? null : extraEventContainers.get(extraId);

			if (extraContainers != null) {
				postToHandlers(scriptType, extraContainers, event, exh);

				if (!scriptType.isStartup()) {
					postToHandlers(ScriptType.STARTUP, extraContainers, event, exh);
				}
			}

			if (eventContainers != null) {
				postToHandlers(scriptType, eventContainers, event, exh);

				if (!scriptType.isStartup()) {
					postToHandlers(ScriptType.STARTUP, eventContainers, event, exh);
				}
			}
		} catch (EventExit exit) {
			if (exit.result.type() == EventResult.Type.ERROR) {
				if (DevProperties.get().debugInfo) {
					((Throwable) exit.result.value()).printStackTrace();
				}

				scriptType.console.handleError((Throwable) exit.result.value(), null, "Error occurred while handling event '" + this + "'");
			} else {
				eventResult = exit.result;

				if (!getHasResult()) {
					scriptType.console.handleError(new IllegalStateException("Event returned result when it's not cancellable"), null, "Error occurred while handling event '" + this + "'");
				}
			}
		}

		event.afterPosted(eventResult);
		return eventResult;
	}

	private void postToHandlers(ScriptType type, EventHandlerContainer[] containers, EventJS event, @Nullable EventExceptionHandler exh) throws EventExit {
		var handler = containers[type.ordinal()];

		if (handler != null) {
			handler.handle(event, exh);
		}
	}

	@Override
	public String toString() {
		return group + "." + name;
	}

	@Override
	public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
		ScriptType type = cx.getProperty("Type", null);

		if (type == null) {
			throw new IllegalStateException("Unknown script type!");
		}

		try {
			if (args.length == 1) {
				listen(type, null, (IEventHandler) Context.jsToJava(cx, args[0], IEventHandler.class));
			} else if (args.length == 2) {
				var handler = (IEventHandler) Context.jsToJava(cx, args[1], IEventHandler.class);

				for (var o : ListJS.orSelf(args[0])) {
					listen(type, o, handler);
				}
			}
		} catch (Exception ex) {
			type.console.error(ex.getLocalizedMessage());
		}

		return null;
	}

	public void forEachListener(ScriptType type, Consumer<EventHandlerContainer> callback) {
		if (eventContainers != null) {
			var c = eventContainers[type.ordinal()];

			while (c != null) {
				callback.accept(c);
				c = c.child;
			}
		}

		if (extraEventContainers != null) {
			for (var entry : extraEventContainers.entrySet()) {
				var c = entry.getValue()[type.ordinal()];

				while (c != null) {
					callback.accept(c);
					c = c.child;
				}
			}
		}
	}

	public Set<Object> findUniqueExtraIds(ScriptType type) {
		var set = new HashSet<>();

		forEachListener(type, c -> {
			if (c.extraId != null) {
				set.add(c.extraId);
			}
		});

		return set;
	}

	@FunctionalInterface
	public interface EventExceptionHandler {
		/**
		 * Handles an exception thrown by an event handler.
		 *
		 * @param event     The event being posted
		 * @param container The event handler container that threw the exception
		 * @param ex        The exception that was thrown
		 * @return <code>null</code> if the exception could be recovered from, otherwise the exception that should be rethrown
		 * @implNote The thrown exception will never be an instance of {@link EventExit} or {@link WrappedException},
		 * as those are already handled by the container itself.
		 */
		Throwable handle(EventJS event, EventHandlerContainer container, Throwable ex);
	}
}

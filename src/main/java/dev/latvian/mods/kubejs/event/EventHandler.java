package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptTypeHolder;
import dev.latvian.mods.kubejs.script.ScriptTypePredicate;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.RhinoException;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.HideFromJS;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <h3>Example</h3>
 * <p><code>public static final EventHandler CLIENT_RIGHT_CLICKED = ItemEvents.GROUP.client("clientRightClicked", () -> ItemClickedEventJS.class).extra(ItemEvents.SUPPORTS_ITEM);</code></p>
 */
public class EventHandler extends BaseFunction {
	private static final TypeInfo EVENT_HANDLER_TYPE_INFO = TypeInfo.of(IEventHandler.class);

	public final EventGroup group;
	public final String name;
	public final ScriptTypePredicate scriptTypePredicate;
	public final Supplier<Class<? extends KubeEvent>> eventType;
	private TypeInfo result;
	public transient Extra<?> extra;
	public transient boolean extraRequired;
	protected EventHandlerContainer[] eventContainers;
	public transient EventExceptionHandler exceptionHandler;

	EventHandler(EventGroup g, String n, ScriptTypePredicate st, Supplier<Class<? extends KubeEvent>> e) {
		this.group = g;
		this.name = n;
		this.scriptTypePredicate = st;
		this.eventType = e;
		this.result = null;
		this.extra = null;
		this.extraRequired = false;
		this.eventContainers = null;
		this.exceptionHandler = null;
	}

	/**
	 * Allow event.cancel() to be called
	 */
	@HideFromJS
	public EventHandler hasResult(TypeInfo result) {
		this.result = result;
		return this;
	}

	public EventHandler hasResult() {
		return hasResult(TypeInfo.NONE);
	}

	@HideFromJS
	@Nullable
	public TypeInfo getResult() {
		return result;
	}

	@HideFromJS
	public EventHandler exceptionHandler(EventExceptionHandler handler) {
		this.exceptionHandler = handler;
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
	}

	public boolean hasListeners() {
		return eventContainers != null;
	}

	public void listen(@Nullable Context cx, ScriptType type, @Nullable Object extraId, IEventHandler handler) {
		if (cx != null) {
			if (!((KubeJSContext) cx).kjsFactory.manager.canListenEvents) {
				throw new IllegalStateException("Event handler '" + this + "' can only be registered during script loading!");
			}
		}

		if (!scriptTypePredicate.test(type)) {
			throw new UnsupportedOperationException("Tried to register event handler '" + this + "' for invalid script type " + type + "! Valid script types: " + scriptTypePredicate.getValidTypes());
		}

		if (extraId != null && extra != null) {
			extraId = Wrapper.unwrapped(extraId);
			extraId = extra.transformer.transform(extraId);
		}

		if (extra != null && extraRequired && extraId == null) {
			throw new IllegalArgumentException("Event handler '" + this + "' requires extra id!");
		}

		if (extra == null && extraId != null) {
			throw new IllegalArgumentException("Event handler '" + this + "' doesn't support extra id!");
		}

		if (extra != null && extraId != null && !extra.validator.test(extraId)) {
			throw new IllegalArgumentException("Event handler '" + this + "' doesn't accept id '" + extra.toString.transform(extraId) + "'!");
		}

		var line = new int[1];
		var source = cx == null ? "java" : Context.getSourcePositionFromStack(cx, line);

		var map = createMap(extraId);
		var index = type.ordinal();

		if (map[index] == null) {
			map[index] = new EventHandlerContainer(extraId, handler, source, line[0]);
		} else {
			map[index].add(extraId, handler, source, line[0]);
		}
	}

	protected EventHandlerContainer[] createMap(@Nullable Object extraId) {
		if (eventContainers == null) {
			eventContainers = new EventHandlerContainer[ScriptType.VALUES.length];
		}

		return eventContainers;
	}

	@HideFromJS
	public void listenJava(ScriptType type, @Nullable Object extraId, IEventHandler handler) {
		listen(null, type, extraId, handler);
	}

	/**
	 * @see EventHandler#post(ScriptTypeHolder, KubeEvent)
	 */
	public EventResult post(KubeEvent event) {
		if (scriptTypePredicate instanceof ScriptTypeHolder type) {
			return postInternal(type, null, event);
		} else {
			throw new IllegalStateException("You must specify which script type to post event to");
		}
	}

	/**
	 * @return EventResult that can contain an object. What previously returned true on {@link KubeEvent#cancel(Context)} ()} now returns {@link EventResult#interruptFalse()}
	 * @see KubeEvent#cancel(Context)
	 * @see KubeEvent#success(Context)
	 * @see KubeEvent#exit(Context)
	 * @see KubeEvent#cancel(Context, Object)
	 * @see KubeEvent#success(Context, Object)
	 * @see KubeEvent#exit(Context, Object)
	 */
	public EventResult post(ScriptTypeHolder scriptType, KubeEvent event) {
		return postInternal(scriptType, null, event);
	}

	protected EventResult postInternal(ScriptTypeHolder type, @Nullable Object extraId, KubeEvent event) {
		if (!hasListeners()) {
			return EventResult.PASS;
		}

		var scriptType = type.kjs$getScriptType();

		if (extra != null && extraRequired && extraId == null) {
			throw new IllegalArgumentException("Event handler '" + this + "' requires extra id!");
		}

		if (extra == null && extraId != null) {
			throw new IllegalArgumentException("Event handler '" + this + "' doesn't support extra id " + extraId + "!");
		}

		var eventResult = EventResult.PASS;

		try {
			var extraContainers = this instanceof SpecializedEventHandler<?> h ? (h.extraEventContainers == null ? null : h.extraEventContainers.get(extraId)) : null;

			if (extraContainers != null) {
				var handler = extraContainers[scriptType.ordinal()];

				if (handler != null) {
					handler.handle(event, exceptionHandler);
				}

				if (!scriptType.isStartup()) {
					handler = extraContainers[ScriptType.STARTUP.ordinal()];

					if (handler != null) {
						handler.handle(event, exceptionHandler);
					}
				}
			}

			if (eventContainers != null) {
				var handler = eventContainers[scriptType.ordinal()];

				if (handler != null) {
					handler.handle(event, exceptionHandler);
				}

				if (!scriptType.isStartup()) {
					handler = eventContainers[ScriptType.STARTUP.ordinal()];

					if (handler != null) {
						handler.handle(event, exceptionHandler);
					}
				}
			}
		} catch (EventExit exit) {
			if (exit.result.type() == EventResult.Type.ERROR) {
				if (DevProperties.get().debugInfo) {
					((Throwable) exit.result.value()).printStackTrace();
				}

				scriptType.console.error("Error in '" + this + "'", (Throwable) exit.result.value());
			} else {
				eventResult = exit.result;

				if (getResult() == null) {
					scriptType.console.error("Error in '" + this + "'", new IllegalStateException("Event returned result when it's not cancellable"));
				}
			}
		} catch (RhinoException error) {
			scriptType.console.error("Error in '" + this + "'", error);
		}

		event.afterPosted(eventResult);
		return eventResult;
	}

	@Override
	public String toString() {
		return group + "." + name;
	}

	@Override
	public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
		ScriptType type = ((KubeJSContext) cx).getType();

		try {
			if (args.length == 1) {
				listen(cx, type, null, (IEventHandler) cx.jsToJava(args[0], EVENT_HANDLER_TYPE_INFO));
			} else if (args.length == 2) {
				var handler = (IEventHandler) cx.jsToJava(args[1], EVENT_HANDLER_TYPE_INFO);

				for (var o : ListJS.orSelf(args[0])) {
					listen(cx, type, o, handler);
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
	}
}

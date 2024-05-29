package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptTypeHolder;
import dev.latvian.mods.kubejs.script.ScriptTypePredicate;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.HideFromJS;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SpecializedEventHandler<E> extends EventHandler {
	protected Map<Object, EventHandlerContainer[]> extraEventContainers;

	SpecializedEventHandler(EventGroup g, String n, ScriptTypePredicate st, Extra<E> extra, Supplier<Class<? extends KubeEvent>> e) {
		super(g, n, st, e);
		this.extra = extra;
		this.extraEventContainers = null;
	}

	/**
	 * Marks this event handler to require extra argument, usually needed for events related to registries
	 */
	public SpecializedEventHandler<E> required() {
		extraRequired = true;
		return this;
	}

	@Override
	@HideFromJS
	public SpecializedEventHandler<E> hasResult() {
		super.hasResult();
		return this;
	}

	@Override
	@HideFromJS
	public SpecializedEventHandler<E> exceptionHandler(EventExceptionHandler handler) {
		this.exceptionHandler = handler;
		return this;
	}

	@Override
	public boolean hasListeners() {
		return eventContainers != null || extraEventContainers != null;
	}

	public boolean hasListeners(@Nullable E extraId) {
		return eventContainers != null || extraId != null && extraEventContainers != null && extraEventContainers.containsKey(extraId);
	}

	/**
	 * @see SpecializedEventHandler#post(ScriptTypeHolder, E, KubeEvent)
	 */
	public EventResult post(KubeEvent event, @Nullable E extraId) {
		if (scriptTypePredicate instanceof ScriptTypeHolder type) {
			return postInternal(type, extraId, event);
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
	public EventResult post(ScriptTypeHolder type, @Nullable E extraId, KubeEvent event) {
		return postInternal(type, extraId, event);
	}

	@Override
	@HideFromJS
	public void clear(ScriptType type) {
		super.clear(type);

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

	@Override
	protected EventHandlerContainer[] createMap(@Nullable Object extraId) {
		if (extraId == null) {
			return super.createMap(extraId);
		}

		if (extraEventContainers == null) {
			extraEventContainers = extra.identity ? new IdentityHashMap<>() : new HashMap<>();
		}

		var map = extraEventContainers.get(extraId);

		//noinspection Java8MapApi
		if (map == null) {
			map = new EventHandlerContainer[ScriptType.VALUES.length];
			extraEventContainers.put(extraId, map);
		}

		return map;
	}

	@Override
	public void forEachListener(ScriptType type, Consumer<EventHandlerContainer> callback) {
		super.forEachListener(type, callback);

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

	public Set<E> findUniqueExtraIds(ScriptType type) {
		if (!hasListeners()) {
			return Set.of();
		}

		var set = new HashSet<E>();

		forEachListener(type, c -> {
			if (c.extraId != null) {
				set.add((E) c.extraId);
			}
		});

		return set;
	}
}

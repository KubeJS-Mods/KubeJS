package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.RhinoException;
import dev.latvian.mods.rhino.util.HideFromJS;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Example
 * <p>
 * <code>public static final EventHandler EVENT = EventHandler.of(ScriptType.SERVER, ItemRightClickEventJS.class).cancellable();</code>
 */
public class EventHandler {
	public static EventHandler of(ScriptType scriptType, Class<? extends EventJS> eventType) {
		return new EventHandler(scriptType, eventType);
	}

	public final ScriptType scriptType;
	public final Class<? extends EventJS> eventType;
	public boolean canCancel;
	public String eventName;
	private IEventHandler[] handlers;

	private EventHandler(ScriptType st, Class<? extends EventJS> e) {
		scriptType = st;
		eventType = e;
		canCancel = false;
		eventName = eventType.getSimpleName().replace("EventJS", "");
		handlers = null;
	}

	/**
	 * Allow event.cancel() to be called
	 */
	@HideFromJS
	public EventHandler cancellable() {
		canCancel = true;
		return this;
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
	public void clear() {
		handlers = null;
	}

	/**
	 * Call this inside {@link KubeJSPlugin#registerEvents()}
	 */
	@HideFromJS
	public void register() {
		scriptType.eventHandlers.put(eventName, this);
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
			return false;
		}

		boolean c = canCancel || event.canCancel();

		for (var handler : handlers) {
			try {
				handler.onEvent(event);

				if (c && event.isCancelled()) {
					return true;
				}
			} catch (RhinoException ex) {
				scriptType.console.error("Error occurred while handling event '" + eventName + "': " + ex.getMessage());
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}

		return false;
	}
}

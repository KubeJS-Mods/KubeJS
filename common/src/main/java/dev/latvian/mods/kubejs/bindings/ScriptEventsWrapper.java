package dev.latvian.mods.kubejs.bindings;

import dev.latvian.mods.kubejs.event.DataEvent;
import dev.latvian.mods.kubejs.event.EventsJS;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public class ScriptEventsWrapper {
	private final EventsJS events;

	public ScriptEventsWrapper(EventsJS e) {
		events = e;
	}

	public void post(String id, @Nullable Object data) {
		events.postToHandlers(id, events.handlers(id), new DataEvent(false, data));
	}

	public void post(String id) {
		post(id, null);
	}

	public boolean postCancellable(String id, @Nullable Object data) {
		return events.postToHandlers(id, events.handlers(id), new DataEvent(true, data));
	}

	public boolean postCancellable(String id) {
		return postCancellable(id, null);
	}
}
package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.event.DataEvent;
import dev.latvian.kubejs.event.EventsJS;
import dev.latvian.kubejs.event.IEventHandler;
import dev.latvian.kubejs.util.ListJS;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class ScriptEventsWrapper
{
	private final EventsJS events;

	public ScriptEventsWrapper(EventsJS e)
	{
		events = e;
	}

	public void listen(Object id, IEventHandler handler)
	{
		for (Object o : ListJS.orSelf(id))
		{
			events.listen(String.valueOf(o), handler);
		}
	}

	public void post(String id, @Nullable Object data)
	{
		events.postToHandlers(id, events.handlers(id), new DataEvent(false, data));
	}

	public void post(String id)
	{
		post(id, null);
	}

	public boolean postCancellable(String id, @Nullable Object data)
	{
		return events.postToHandlers(id, events.handlers(id), new DataEvent(true, data));
	}

	public boolean postCancellable(String id)
	{
		return postCancellable(id, null);
	}
}
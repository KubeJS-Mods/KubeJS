package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.documentation.DisplayName;
import dev.latvian.kubejs.documentation.Info;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.event.DataEvent;
import dev.latvian.kubejs.event.EventsJS;
import dev.latvian.kubejs.event.IEventHandler;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
@DisplayName("Events")
@Info("This class registers event listeners")
public class ScriptEventsWrapper
{
	@Info("This method will register event listener, and callback function will be called when event is fired form mod")
	public void listen(@P("eventID") String id, @P("handler") IEventHandler handler)
	{
		EventsJS.listen(id, handler);
	}

	@Info("This method will register one event listener for multiple events")
	public void listenAll(@P("eventIDs") String[] ids, @P("handler") IEventHandler handler)
	{
		for (String s : ids)
		{
			listen(s, handler);
		}
	}

	public void post(@P("eventID") String id, @P("data") @Nullable Object data)
	{
		EventsJS.post(id, new DataEvent(false, data));
	}

	public void post(@P("eventID") String id)
	{
		post(id, null);
	}

	public boolean postCancellable(@P("eventID") String id, @P("data") @Nullable Object data)
	{
		return EventsJS.post(id, new DataEvent(true, data));
	}

	public boolean postCancellable(@P("eventID") String id)
	{
		return postCancellable(id, null);
	}
}
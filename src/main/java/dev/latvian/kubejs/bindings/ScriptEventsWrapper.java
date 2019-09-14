package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.documentation.DisplayName;
import dev.latvian.kubejs.documentation.Info;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.event.EventsJS;
import dev.latvian.kubejs.event.IEventHandler;

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
}
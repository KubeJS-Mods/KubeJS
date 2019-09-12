package dev.latvian.kubejs.event;

import dev.latvian.kubejs.documentation.DocClass;
import dev.latvian.kubejs.documentation.DocMethod;

/**
 * @author LatvianModder
 */
@DocClass(value = "This class registers event listeners", displayName = "Events")
public class ScriptEventsWrapper
{
	@DocMethod(value = "This method will register event listener, and callback function will be called when event is fired form mod")
	public void listen(String id, IEventHandler handler)
	{
		EventsJS.listen(id, handler);
	}

	@DocMethod(value = "This method will register one event listener for multiple events")
	public void listenAll(String[] ids, IEventHandler handler)
	{
		for (String s : ids)
		{
			listen(s, handler);
		}
	}
}
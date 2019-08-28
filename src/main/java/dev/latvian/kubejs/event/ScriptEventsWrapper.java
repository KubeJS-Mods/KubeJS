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
		EventsJS.INSTANCE.listen(id, handler);
	}
}
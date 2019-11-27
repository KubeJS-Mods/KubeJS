package dev.latvian.kubejs.documentation;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.script.DataType;
import net.minecraftforge.eventbus.api.Event;

/**
 * @author LatvianModder
 */
public class DocumentationEvent extends Event
{
	private final Documentation documentation;

	public DocumentationEvent(Documentation d)
	{
		documentation = d;
	}

	public void registerAttachedData(DataType type, String name, Class dataClass)
	{
		documentation.registerAttachedData(type, name, dataClass);
	}

	public void registerCustomName(String name, Class... classes)
	{
		documentation.registerCustomName(name, classes);
	}

	public DocumentedEvent registerEvent(String id, Class<? extends EventJS> event)
	{
		DocumentedEvent e = new DocumentedEvent(id, event);
		documentation.registerEvent(e);
		return e;
	}
}
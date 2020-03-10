package dev.latvian.kubejs.documentation;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.script.DataType;
import net.minecraftforge.eventbus.api.Event;

/**
 * @author LatvianModder
 */
public class DocumentationEvent extends Event
{
	public void registerAttachedData(DataType type, String name, Class dataClass)
	{
	}

	public void registerCustomName(String name, Class... classes)
	{
	}

	public DocumentedEvent registerEvent(String id, Class<? extends EventJS> event)
	{
		return new DocumentedEvent(id, event);
	}
}
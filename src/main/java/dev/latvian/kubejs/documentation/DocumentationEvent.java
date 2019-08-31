package dev.latvian.kubejs.documentation;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.script.DataType;
import net.minecraftforge.fml.common.eventhandler.Event;

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

	public void registerEvent(String id, Class<? extends EventJS> event)
	{
		documentation.registerEvent(id, event);
	}

	public void registerDoubleEvent(String id, String extra, Class<? extends EventJS> event)
	{
		registerEvent(id + ".<" + extra + ">", event);
		registerEvent(id, event);
	}
}
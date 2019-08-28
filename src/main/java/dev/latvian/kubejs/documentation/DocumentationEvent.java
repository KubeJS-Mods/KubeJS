package dev.latvian.kubejs.documentation;

import dev.latvian.kubejs.event.EventJS;
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

	public void register(Class documentedClass)
	{
		documentation.register(documentedClass);
	}

	public void registerPackage(Package p)
	{
		DocPackage d = p.getAnnotation(DocPackage.class);

		if (d != null)
		{
			for (Class c : d.value())
			{
				register(c);
			}
		}
	}

	public void registerNative(String name, Class... classes)
	{
		documentation.registerNative(name, classes);
	}

	public void registerEvent(String id, Class<? extends EventJS> event)
	{
		documentation.registerEvent(id, event);
	}
}
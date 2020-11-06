package dev.latvian.kubejs.docs;

import net.minecraftforge.eventbus.api.Event;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class DocumentationEvent extends Event
{
	private final Map<Class<?>, TypeDefinition> types;

	public DocumentationEvent()
	{
		types = new HashMap<>();
	}

	public TypeDefinition type(Class<?> c)
	{
		return types.computeIfAbsent(c, c1 -> new TypeDefinition(this, c1));
	}

	public void createFile()
	{
	}
}
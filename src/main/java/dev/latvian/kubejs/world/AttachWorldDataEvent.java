package dev.latvian.kubejs.world;

import dev.latvian.kubejs.script.AttachDataEvent;
import dev.latvian.kubejs.script.DataType;

import java.util.Map;

/**
 * @author LatvianModder
 */
public class AttachWorldDataEvent extends AttachDataEvent<WorldJS>
{
	public AttachWorldDataEvent(WorldJS p, Map<String, Object> d)
	{
		super(DataType.WORLD, p, d);
	}
}
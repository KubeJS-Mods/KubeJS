package dev.latvian.kubejs.world;

import dev.latvian.kubejs.script.AttachDataEvent;
import dev.latvian.kubejs.script.DataType;

/**
 * @author LatvianModder
 */
public class AttachWorldDataEvent extends AttachDataEvent<WorldJS>
{
	public AttachWorldDataEvent(WorldJS w)
	{
		super(DataType.WORLD, w, w.data);
	}
}
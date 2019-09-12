package dev.latvian.kubejs.server;

import dev.latvian.kubejs.script.AttachDataEvent;
import dev.latvian.kubejs.script.DataType;

/**
 * @author LatvianModder
 */
public class AttachServerDataEvent extends AttachDataEvent<ServerJS>
{
	public AttachServerDataEvent(ServerJS s)
	{
		super(DataType.SERVER, s);
	}
}
package dev.latvian.kubejs.server;

import dev.latvian.kubejs.script.AttachDataEvent;
import dev.latvian.kubejs.script.DataType;

import java.util.Map;

/**
 * @author LatvianModder
 */
public class AttachServerDataEvent extends AttachDataEvent<ServerJS>
{
	public AttachServerDataEvent(ServerJS p, Map<String, Object> d)
	{
		super(DataType.SERVER, p, d);
	}
}
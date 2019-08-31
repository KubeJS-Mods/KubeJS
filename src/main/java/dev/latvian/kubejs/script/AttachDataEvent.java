package dev.latvian.kubejs.script;

import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.Map;

/**
 * @author LatvianModder
 */
public class AttachDataEvent<T> extends Event
{
	private final DataType<T> type;
	private final T parent;
	private final Map<String, Object> data;

	public AttachDataEvent(DataType<T> t, T p, Map<String, Object> d)
	{
		type = t;
		parent = p;
		data = d;
	}

	public DataType<T> getType()
	{
		return type;
	}

	public T getParent()
	{
		return parent;
	}

	public void add(String id, Object object)
	{
		data.put(id, object);
	}
}
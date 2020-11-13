package dev.latvian.kubejs.script;

import dev.latvian.kubejs.util.WithAttachedData;

/**
 * @author LatvianModder
 */
public class AttachDataEvent<T extends WithAttachedData>
{
	private final DataType<T> type;
	private final T parent;

	public AttachDataEvent(DataType<T> t, T p)
	{
		type = t;
		parent = p;
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
		parent.getData().put(id, object);
	}
}
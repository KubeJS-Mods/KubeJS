package dev.latvian.kubejs.util;

import java.util.HashMap;

/**
 * @author LatvianModder
 */
public class AttachedData extends HashMap<String, Object>
{
	private final Object parent;

	public AttachedData(Object p)
	{
		parent = p;
	}

	public Object getParent()
	{
		return parent;
	}
}
package dev.latvian.kubejs.script;

import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.Map;

/**
 * @author LatvianModder
 */
public class BindingsEvent extends Event
{
	private final Map<String, Object> map;

	public BindingsEvent(Map<String, Object> m)
	{
		map = m;
	}

	public void add(String name, Object value)
	{
		map.put(name, value);
	}
}
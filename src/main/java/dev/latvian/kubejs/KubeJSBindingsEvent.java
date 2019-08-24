package dev.latvian.kubejs;

import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.Map;

/**
 * @author LatvianModder
 */
public class KubeJSBindingsEvent extends Event
{
	private final Map<String, Object> map;

	public KubeJSBindingsEvent(Map<String, Object> m)
	{
		map = m;
	}

	public void add(String name, Object value)
	{
		map.put(name, value);
	}
}
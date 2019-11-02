package dev.latvian.kubejs.script;

import dev.latvian.kubejs.util.FunctionBinding;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.Map;

/**
 * @author LatvianModder
 */
public class BindingsEvent extends Event
{
	private final Map<String, Object> map;
	private final Map<String, Object> constantMap;

	public BindingsEvent(Map<String, Object> m, Map<String, Object> cm)
	{
		map = m;
		constantMap = cm;
	}

	public void add(String name, Object value)
	{
		map.put(name, value);
	}

	public void addFunction(String name, FunctionBinding.Handler handler)
	{
		add(name, new FunctionBinding(handler));
	}

	public void addConstant(String name, Object value)
	{
		constantMap.put(name, value);
	}
}
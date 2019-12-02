package dev.latvian.kubejs.script;

import dev.latvian.kubejs.util.FunctionBinding;
import jdk.nashorn.api.scripting.JSObject;
import net.minecraftforge.eventbus.api.Event;

import java.util.Map;

/**
 * @author LatvianModder
 */
public class BindingsEvent extends Event
{
	public final ScriptType type;
	private final Map<String, Object> map;
	private final Map<String, Object> constantMap;

	public BindingsEvent(ScriptType t, Map<String, Object> m, Map<String, Object> cm)
	{
		type = t;
		map = m;
		constantMap = cm;
	}

	public ScriptType getType()
	{
		return type;
	}

	public void add(String name, Object value)
	{
		map.put(name, value);
	}

	public void addFunction(String name, FunctionBinding.Handler handler)
	{
		add(name, new FunctionBinding(handler));
	}

	public void addFunction(String name, JSObject function)
	{
		add(name, function);
	}

	public void addConstant(String name, Object value)
	{
		constantMap.put(name, value);
	}

	public boolean isServer()
	{
		return false;
	}
}
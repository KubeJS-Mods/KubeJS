package dev.latvian.kubejs.script;

import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaClass;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.ScriptableObject;
import dev.latvian.mods.rhino.util.DynamicFunction;
import net.minecraftforge.eventbus.api.Event;

/**
 * @author LatvianModder
 */
public class BindingsEvent extends Event
{
	public final ScriptType type;
	public Scriptable scope;

	public BindingsEvent(ScriptType t, Scriptable s)
	{
		type = t;
		scope = s;
	}

	public ScriptType getType()
	{
		return type;
	}

	public void add(String name, Object value)
	{
		ScriptableObject.putProperty(scope, name, Context.javaToJS(value, scope));
	}

	public void addClass(String name, Class<?> clazz)
	{
		add(name, new NativeJavaClass(scope, clazz));
	}

	public void addFunction(String name, DynamicFunction.Callback callback)
	{
		add(name, new DynamicFunction(callback));
	}

	public void addConstant(String name, Object value)
	{
		add(name, value);
	}

	public void addFunction(String name, BaseFunction function)
	{
		add(name, function);
	}
}
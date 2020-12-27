package dev.latvian.kubejs.script;

import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaClass;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.ScriptableObject;
import dev.latvian.mods.rhino.util.DynamicFunction;
import me.shedaniel.architectury.ForgeEvent;
import me.shedaniel.architectury.event.Event;
import me.shedaniel.architectury.event.EventFactory;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
@ForgeEvent
public class BindingsEvent
{
	public static final Event<Consumer<BindingsEvent>> EVENT = EventFactory.createConsumerLoop(BindingsEvent.class);
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

	public void addFunction(String name, DynamicFunction.Callback callback, Class... types)
	{
		add(name, new TypedDynamicFunction(callback, types));
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
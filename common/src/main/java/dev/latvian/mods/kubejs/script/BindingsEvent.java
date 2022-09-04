package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.SharedContextData;
import dev.latvian.mods.rhino.util.DynamicFunction;

/**
 * @author LatvianModder
 */
public class BindingsEvent {
	public final ScriptManager manager;
	public final ScriptType type;
	public final SharedContextData contextData;

	public BindingsEvent(ScriptManager m, SharedContextData d) {
		manager = m;
		type = manager.type;
		contextData = d;
	}

	public ScriptType getType() {
		return type;
	}

	public void add(String name, Object value) {
		if (value != null) {
			contextData.addToTopLevelScope(name, value);
		}
	}

	public void addFunction(String name, DynamicFunction.Callback callback) {
		add(name, new DynamicFunction(callback));
	}

	public void addFunction(String name, DynamicFunction.Callback callback, Class<?>... types) {
		add(name, new TypedDynamicFunction(callback, types));
	}

	public void addFunction(String name, BaseFunction function) {
		add(name, function);
	}
}
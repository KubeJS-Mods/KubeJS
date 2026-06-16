package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.ScriptableObject;

import java.util.function.Supplier;

public record BindingRegistry(KubeJSContext context, Scriptable scope) {
	public ScriptType type() {
		return context.getType();
	}

	public void add(String name, Object value) {
		if (value != null) {
			context.addToScope(scope, name, value);
		}
	}

	public void addLazy(String name, Supplier<?> supplier) {
		var descriptor = context.newObject(scope);
		descriptor.put(context, "get", descriptor, new BaseFunction(scope, ScriptableObject.getFunctionPrototype(scope, context)) {
			private Object value;

			@Override
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				if (value == null) {
					var supplied = supplier.get();

					if (supplied != null) {
						value = supplied;
					}

					return supplied;
				}

				return value;
			}
		});
		descriptor.put(context, "enumerable", descriptor, true);
		descriptor.put(context, "configurable", descriptor, true);

		if (scope instanceof ScriptableObject object && descriptor instanceof ScriptableObject descriptorObject) {
			object.defineOwnProperty(context, name, descriptorObject);
		}
	}
}
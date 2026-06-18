package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.kubejs.util.Lazy;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaClass;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.ScriptableObject;

import java.util.function.Supplier;

public record BindingRegistry(KubeJSContext context, Scriptable scope) {
	public ScriptType type() {
		return context.getType();
	}

	/// Adds a value to the script scope when it is not null.
	public void add(String name, Object value) {
		if (value != null) {
			context.addToScope(scope, name, value);
		}
	}

	/// Adds a property with a getter that lazily resolves the supplied value when read.
	/// Null results are not cached, allowing a later read to resolve the value again.
	public void addLazy(String name, Supplier<?> supplier) {
		var lazy = Lazy.of(() -> {
			var value = supplier.get();

			if (value == null) {
				return null;
			}

			return value instanceof Class<?> c ? new NativeJavaClass(context, BindingRegistry.this.scope, c) : context.javaToJS(value, BindingRegistry.this.scope);
		});

		if (scope instanceof ScriptableObject object) {
			var getter = new BaseFunction(scope, ScriptableObject.getFunctionPrototype(scope, context)) {
				@Override
				public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
					var value = lazy.get();

					if (value == null) {
						// Avoid caching early null values forever, e.g., Minecraft#getInstance before client init
						lazy.forget();
						return null;
					}

					// Hand it over to add() as a normal global once the lazy getter is no longer needed
					object.delete(context, name);
					add(name, value);
					return value;
				}
			};

			// Allow scripters to reference the binding before the value exists via the temporary getter
			object.setGetterOrSetter(context, name, 0, getter, false);
			object.setAttributes(context, name, ScriptableObject.EMPTY);
		}
	}
}
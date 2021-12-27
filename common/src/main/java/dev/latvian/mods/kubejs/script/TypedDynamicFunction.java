package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.util.DynamicFunction;

/**
 * @author LatvianModder
 */
public class TypedDynamicFunction extends DynamicFunction {
	private final Class[] types;

	public TypedDynamicFunction(Callback f, Class[] t) {
		super(f);
		types = t;
	}

	@Override
	public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
		if (args.length != types.length) {
			throw new IllegalArgumentException("Argument length doesn't match required " + types.length + "!");
		}

		var newArgs = new Object[types.length];

		for (var i = 0; i < types.length; i++) {
			newArgs[i] = (types[i] == null || types[i] == Object.class) ? args[i] : Context.jsToJava(args[i], types[i]);
		}

		return super.call(cx, scope, thisObj, newArgs);
	}
}

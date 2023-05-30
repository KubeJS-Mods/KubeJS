package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.recipe.schema.RecipeNamespace;
import dev.latvian.mods.kubejs.util.WrappedJS;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;

import java.util.Map;

public class NamespaceFunction extends BaseFunction implements WrappedJS {
	private final RecipeNamespace namespace;
	private final Map<String, RecipeTypeFunction> map;

	public NamespaceFunction(RecipeNamespace namespace, Map<String, RecipeTypeFunction> map) {
		this.namespace = namespace;
		this.map = map;
	}

	@Override
	public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args0) {
		return namespace.keySet();
	}

	@Override
	public Object get(Context cx, String name, Scriptable start) {
		return map.get(name);
	}

	@Override
	public String toString() {
		return namespace.name;
	}

	public String getMod() {
		return namespace.name;
	}

	@Override
	public int hashCode() {
		return namespace.name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return namespace.name.equals(obj.toString());
	}
}
package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.rhino.type.TypeInfo;

public interface TypeDescriptionRegistry {
	ScriptType scriptType();

	void register(Class<?> target, TypeInfo typeInfo);
}

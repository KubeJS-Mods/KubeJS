package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.rhino.type.TypeInfo;

public interface TypeDescriptionRegistry {
	ScriptType scriptType();

	<T> void register(Class<T> target, TypeInfo typeInfo);
}

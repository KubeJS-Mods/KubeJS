package dev.latvian.kubejs;

import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.ClassList;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;

public interface KubeJSPlugin {
	default void init() {

	}

	default void afterInit() {
	}

	default void addClasses(ScriptType type, ClassList list) {
	}

	default void addBindings(BindingsEvent event) {
	}

	default void addTypeWrappers(ScriptType type, TypeWrappers typeWrappers) {
	}
}

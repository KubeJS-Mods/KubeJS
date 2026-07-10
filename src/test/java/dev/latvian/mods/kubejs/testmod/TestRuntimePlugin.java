package dev.latvian.mods.kubejs.testmod;

import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;

public class TestRuntimePlugin implements KubeJSPlugin {
	@Override
	public void registerBindings(BindingRegistry bindings) {
		bindings.add("TestRuntime", TestRuntime.class);
	}
}

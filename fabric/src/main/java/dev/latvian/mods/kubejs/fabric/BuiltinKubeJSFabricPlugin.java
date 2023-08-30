package dev.latvian.mods.kubejs.fabric;

import dev.latvian.mods.kubejs.BuiltinKubeJSPlugin;
import dev.latvian.mods.kubejs.item.fabric.custom.MultitoolItemJSFabric;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ClassFilter;

public class BuiltinKubeJSFabricPlugin extends BuiltinKubeJSPlugin {
	@Override
	public void init() {
		super.init();

		RegistryInfo.ITEM.addType("multitool", MultitoolItemJSFabric.Builder.class, MultitoolItemJSFabric.Builder::new);
	}
	@Override
	public void registerClasses(ScriptType type, ClassFilter filter) {
		super.registerClasses(type, filter);

		filter.allow("net.fabricmc");
		filter.deny("net.fabricmc.accesswidener");
		filter.deny("net.fabricmc.devlaunchinjector");
		filter.deny("net.fabricmc.loader");
		filter.deny("net.fabricmc.tinyremapper");

		filter.deny("com.chocohead.mm"); // Manningham Mills
	}
}

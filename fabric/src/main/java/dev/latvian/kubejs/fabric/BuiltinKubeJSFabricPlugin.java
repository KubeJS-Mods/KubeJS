package dev.latvian.kubejs.fabric;

import dev.latvian.kubejs.BuiltinKubeJSPlugin;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.ClassList;

public class BuiltinKubeJSFabricPlugin extends BuiltinKubeJSPlugin {
	@Override
	public void addClasses(ScriptType type, ClassList list) {
		super.addClasses(type, list);

		list.allow("net.fabricmc");
		list.deny("net.fabricmc.accesswidener");
		list.deny("net.fabricmc.devlaunchinjector");
		list.deny("net.fabricmc.loader");
		list.deny("net.fabricmc.tinyremapper");

		list.deny("com.chocohead.mm"); // Manningham Mills
	}
}

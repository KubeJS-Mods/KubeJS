package dev.latvian.mods.kubejs.neoforge;

import dev.latvian.mods.kubejs.BuiltinKubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ClassFilter;
import net.neoforged.neoforge.common.NeoForge;

public class NeoForgeKubeJSPlugin extends BuiltinKubeJSPlugin {
	@Override
	public void registerClasses(ScriptType type, ClassFilter filter) {
		super.registerClasses(type, filter);

		filter.allow("net.neoforged"); // Forge
		filter.deny("net.neoforged.fml");
		filter.deny("net.neoforged.accesstransformer");
		filter.deny("net.neoforged.coremod");

		filter.deny("cpw.mods.modlauncher"); // FML
		filter.deny("cpw.mods.gross");
	}

	@Override
	public void registerBindings(BindingsEvent event) {
		super.registerBindings(event);

		if (event.type().isStartup()) {
			event.add("NativeEvents", new NativeEventWrapper("NativeEvents", NeoForge.EVENT_BUS));
			KubeJSEntryPoint.eventBus().ifPresent(bus -> event.add("NativeModEvents", new NativeEventWrapper("NativeModEvents", bus)));
		}
	}
}

package dev.latvian.mods.kubejs.neoforge;

import dev.latvian.mods.kubejs.BuiltinKubeJSPlugin;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.integration.jei.JEIEvents;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.WrapperRegistry;
import dev.latvian.mods.kubejs.util.ClassFilter;
import net.minecraft.core.component.DataComponentPatch;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidStack;

public class NeoForgeKubeJSPlugin extends BuiltinKubeJSPlugin {
	@Override
	public void registerEvents() {
		super.registerEvents();

		if (ModList.get().isLoaded("jei")) {
			JEIEvents.register();
		}
	}

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

		if (event.getType().isStartup()) {
			event.add("NativeEvents", new NativeEventWrapper("NativeEvents", NeoForge.EVENT_BUS));
			KubeJSEntryPoint.eventBus().ifPresent(bus -> event.add("NativeModEvents", new NativeEventWrapper("NativeModEvents", bus)));
		}
	}

	@Override
	public void registerTypeWrappers(WrapperRegistry registry) {
		super.registerTypeWrappers(registry);

		registry.registerSimple(FluidStack.class, o -> {
			var fs = FluidStackJS.of(o);
			return fs.kjs$isEmpty() ? FluidStack.EMPTY : new FluidStack(fs.getFluid().builtInRegistryHolder(), (int) fs.kjs$getAmount(), DataComponentPatch.EMPTY);
		});
	}
}

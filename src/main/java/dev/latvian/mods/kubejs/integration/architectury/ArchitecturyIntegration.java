package dev.latvian.mods.kubejs.integration.architectury;

import dev.architectury.hooks.fluid.forge.FluidStackHooksForge;
import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.TypeWrapperRegistry;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;

public class ArchitecturyIntegration implements KubeJSPlugin {
	public static dev.architectury.fluid.FluidStack wrapArchFluid(RegistryAccessContainer registries, Object o) {
		return FluidStackHooksForge.fromForge(FluidWrapper.wrap(registries, o));
	}

	@Override
	public void registerTypeWrappers(TypeWrapperRegistry registry) {
		registry.register(dev.architectury.fluid.FluidStack.class, ArchitecturyIntegration::wrapArchFluid);
	}
}
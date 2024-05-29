package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.level.material.Fluid;

@RemapPrefixForJS("kjs$")
public interface FluidKJS extends WithRegistryKeyKJS<Fluid> {
	default Fluid kjs$self() {
		return (Fluid) this;
	}

	@Override
	default RegistryInfo<Fluid> kjs$getKubeRegistry() {
		return RegistryInfo.FLUID;
	}
}

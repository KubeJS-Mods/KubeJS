package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.fluid.FluidLike;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidType;

@RemapPrefixForJS("kjs$")
public interface FluidKJS extends RegistryObjectKJS<Fluid>, FluidLike {
	@Override
	default Fluid kjs$getFluid() {
		return (Fluid) this;
	}

	@Override
	default RegistryInfo<Fluid> kjs$getKubeRegistry() {
		return RegistryInfo.FLUID;
	}

	@Override
	default int kjs$getAmount() {
		return FluidType.BUCKET_VOLUME;
	}

	@Override
	default boolean kjs$isEmpty() {
		return kjs$getFluid().isSame(Fluids.EMPTY);
	}
}

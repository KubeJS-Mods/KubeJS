package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.fluid.FluidLike;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
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
	default ResourceKey<Registry<Fluid>> kjs$getRegistryId() {
		return Registries.FLUID;
	}

	@Override
	default Registry<Fluid> kjs$getRegistry() {
		return BuiltInRegistries.FLUID;
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

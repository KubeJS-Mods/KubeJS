package dev.latvian.mods.kubejs.fluid;

import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import dev.latvian.mods.kubejs.util.BuilderBase;
import net.minecraft.world.level.material.Fluid;

public class FlowingFluidBuilder extends BuilderBase<Fluid> {
	public final FluidBuilder fluidBuilder;

	public FlowingFluidBuilder(FluidBuilder b) {
		super(b.newID("flowing_", ""));
		fluidBuilder = b;
	}

	@Override
	public RegistryObjectBuilderTypes<Fluid> getRegistryType() {
		return RegistryObjectBuilderTypes.FLUID;
	}

	@Override
	public Fluid createObject() {
		return KubeJSFluidEventHandler.buildFluid(false, fluidBuilder);
	}
}

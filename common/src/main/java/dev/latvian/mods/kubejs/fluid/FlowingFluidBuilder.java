package dev.latvian.mods.kubejs.fluid;

import dev.architectury.core.fluid.ArchitecturyFlowingFluid;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.world.level.material.FlowingFluid;

public class FlowingFluidBuilder extends BuilderBase<FlowingFluid> {
	public final FluidBuilder fluidBuilder;

	public FlowingFluidBuilder(FluidBuilder b) {
		super(b.newID("flowing_", ""));
		fluidBuilder = b;
	}

	@Override
	public final RegistryInfo getRegistryType() {
		return RegistryInfo.FLUID;
	}

	@Override
	public FlowingFluid createObject() {
		return new ArchitecturyFlowingFluid.Flowing(fluidBuilder.createAttributes());
	}
}

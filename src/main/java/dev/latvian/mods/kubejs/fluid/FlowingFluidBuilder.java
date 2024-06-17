package dev.latvian.mods.kubejs.fluid;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.world.level.material.FlowingFluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

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
		return new BaseFlowingFluid.Flowing(fluidBuilder.createProperties());
	}
}

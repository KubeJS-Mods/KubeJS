package dev.latvian.mods.kubejs.fluid;

import dev.architectury.core.fluid.ArchitecturyFlowingFluid;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;

import java.util.function.Supplier;

public class FlowingFluidBuilder extends BuilderBase implements Supplier<FlowingFluid> {
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
	public Fluid createObject() {
		return new ArchitecturyFlowingFluid.Flowing(fluidBuilder.createAttributes());
	}

	@Override
	public FlowingFluid get() {
		return getObject();
	}
}

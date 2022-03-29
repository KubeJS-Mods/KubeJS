package dev.latvian.mods.kubejs.fluid;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

public class BasicFluidBuilder extends FluidBuilder {
	public BasicFluidBuilder(ResourceLocation i) {
		super(i);
	}

	@Override
	public Fluid createObject() {
		// FIXME
		throw new IllegalStateException("Fluid registry not possible rn!");
	}
}

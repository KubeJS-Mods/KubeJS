package dev.latvian.mods.kubejs.fluid;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.block.BlockRenderType;
import net.minecraft.resources.ResourceLocation;

public class ThinFluidBuilder extends FluidBuilder {
	public ThinFluidBuilder(ResourceLocation i) {
		super(i);
		fluidType.tint(WATER_COLOR);
		fluidType.stillTexture(KubeJS.id("block/thin_fluid_still"));
		fluidType.flowingTexture(KubeJS.id("block/thin_fluid_flow"));
		fluidType.renderType(BlockRenderType.TRANSLUCENT);
		fluidType.fallDistanceModifier(0F);
	}
}
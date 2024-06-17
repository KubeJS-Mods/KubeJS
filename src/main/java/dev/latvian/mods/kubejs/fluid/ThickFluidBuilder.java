package dev.latvian.mods.kubejs.fluid;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.block.BlockRenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.common.SoundActions;

public class ThickFluidBuilder extends FluidBuilder {
	public ThickFluidBuilder(ResourceLocation i) {
		super(i);
		fluidType.tint(WATER_COLOR);
		fluidType.stillTexture(KubeJS.id("block/thick_fluid_still"));
		fluidType.flowingTexture(KubeJS.id("block/thick_fluid_flow"));
		fluidType.renderType(BlockRenderType.SOLID);
		fluidType.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA);
		fluidType.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA);
		fluidType.canSwim(false);
		fluidType.canDrown(false);
		fluidType.density(3000);
		fluidType.viscosity(6000);
		slopeFindDistance(2);
		tickRate(20);
		// fluidType.motionScale(0.0023D);
	}
}
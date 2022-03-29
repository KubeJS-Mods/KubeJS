package dev.latvian.mods.kubejs.block.fabric;

import dev.latvian.mods.kubejs.fluid.FluidBuilder;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;

public class KubeJSBlockEventHandlerImpl {
	public static LiquidBlock buildFluidBlock(FluidBuilder builder, BlockBehaviour.Properties properties) {
		return new LiquidBlock((FlowingFluid) builder.get(), properties) {
		};
	}
}

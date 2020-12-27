package dev.latvian.kubejs.block.forge;

import dev.latvian.kubejs.fluid.FluidBuilder;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class KubeJSBlockEventHandlerImpl
{
	public static LiquidBlock buildFluidBlock(FluidBuilder builder, BlockBehaviour.Properties properties)
	{
		return new LiquidBlock(() -> builder.stillFluid, properties);
	}
}

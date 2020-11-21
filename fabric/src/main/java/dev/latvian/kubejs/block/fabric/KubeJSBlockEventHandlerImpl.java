package dev.latvian.kubejs.block.fabric;

import dev.latvian.kubejs.fluid.FluidBuilder;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class KubeJSBlockEventHandlerImpl
{
	public static LiquidBlock build(FluidBuilder builder, BlockBehaviour.Properties properties)
	{
		return new LiquidBlock(builder.stillFluid, properties)
		{
		};
	}
}

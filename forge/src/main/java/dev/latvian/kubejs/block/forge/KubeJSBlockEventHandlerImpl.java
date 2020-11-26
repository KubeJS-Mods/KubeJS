package dev.latvian.kubejs.block.forge;

import dev.latvian.kubejs.fluid.FluidBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.FlowingFluidBlock;

public class KubeJSBlockEventHandlerImpl
{
	public static FlowingFluidBlock buildFluidBlock(FluidBuilder builder, AbstractBlock.Properties properties)
	{
		return new FlowingFluidBlock(() -> builder.stillFluid, properties);
	}
}

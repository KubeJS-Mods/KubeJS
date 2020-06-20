package dev.latvian.kubejs.fluid;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class FluidJS extends ForgeFlowingFluid.Source
{
	public final FluidBuilder properties;

	public FluidJS(FluidBuilder b, Supplier<Fluid> f, Supplier<Item> i)
	{
		super(b.createProperties(f, i));
		properties = b;
	}
}

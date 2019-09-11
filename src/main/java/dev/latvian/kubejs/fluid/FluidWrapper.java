package dev.latvian.kubejs.fluid;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class FluidWrapper
{
	public FluidStackJS of(Object o)
	{
		return FluidStackJS.of(o);
	}

	@Nullable
	public Fluid getType(Object id)
	{
		return FluidRegistry.getFluid(String.valueOf(id));
	}
}
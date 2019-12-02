package dev.latvian.kubejs.fluid;

import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;

/**
 * @author LatvianModder
 */
public class FluidWrapper
{
	public FluidStackJS of(Object o)
	{
		return FluidStackJS.of(o);
	}

	public FluidStackJS of(Object o, Object amountOrNBT)
	{
		return FluidStackJS.of(o, amountOrNBT);
	}

	public FluidStackJS of(Object o, int amount, Object nbt)
	{
		return FluidStackJS.of(o, amount, nbt);
	}

	public Fluid getType(Object id)
	{
		Fluid f = ForgeRegistries.FLUIDS.getValue(UtilsJS.getID(id));
		return f == null ? Fluids.EMPTY : f;
	}

	public Set<ResourceLocation> getTypes()
	{
		return ForgeRegistries.FLUIDS.getKeys();
	}
}
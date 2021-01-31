package dev.latvian.kubejs.fluid;

import dev.latvian.kubejs.docs.MinecraftClass;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class FluidWrapper
{
	public FluidStackJS of(ResourceLocation o)
	{
		return FluidStackJS.of(o);
	}

	public FluidStackJS of(ResourceLocation o, Object amountOrNBT)
	{
		return FluidStackJS.of(o, amountOrNBT);
	}

	public FluidStackJS of(ResourceLocation o, int amount, Object nbt)
	{
		return FluidStackJS.of(o, amount, nbt);
	}

	@MinecraftClass
	public Fluid getType(ResourceLocation id)
	{
		return Registry.FLUID.get(id);
	}

	public List<String> getTypes()
	{
		List<String> types = new ArrayList<>();

		for (ResourceLocation id : Registry.FLUID.keySet())
		{
			types.add(id.toString());
		}

		return types;
	}

	public FluidStackJS getEmpty()
	{
		return EmptyFluidStackJS.INSTANCE;
	}
}
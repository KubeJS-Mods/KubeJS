package dev.latvian.kubejs.fluid;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.docs.ID;
import dev.latvian.kubejs.docs.MinecraftClass;
import dev.latvian.kubejs.util.UtilsJS;
import me.shedaniel.architectury.registry.Registries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class FluidWrapper
{
	public FluidStackJS of(@ID Object o)
	{
		return FluidStackJS.of(o);
	}

	public FluidStackJS of(@ID Object o, Object amountOrNBT)
	{
		return FluidStackJS.of(o, amountOrNBT);
	}

	public FluidStackJS of(@ID Object o, int amount, Object nbt)
	{
		return FluidStackJS.of(o, amount, nbt);
	}

	@MinecraftClass
	public Fluid getType(@ID String id)
	{
		Fluid fluid = Registries.get(KubeJS.MOD_ID).get(Registry.FLUID_REGISTRY).get(UtilsJS.getMCID(id));
		return fluid == null ? Fluids.EMPTY : fluid;
	}

	public List<String> getTypes()
	{
		List<String> types = new ArrayList<>();

		for (ResourceLocation id : Registries.get(KubeJS.MOD_ID).get(Registry.FLUID_REGISTRY).getIds())
		{
			types.add(id.toString());
		}

		return types;
	}
}
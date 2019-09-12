package dev.latvian.kubejs.fluid;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

	public List<String> getList()
	{
		return new ArrayList<>(FluidRegistry.getRegisteredFluids().keySet());
	}

	public Map<String, Fluid> getTypeMap()
	{
		return FluidRegistry.getRegisteredFluids();
	}
}
package dev.latvian.kubejs.fluid;

import dev.latvian.kubejs.util.MapJS;
import me.shedaniel.architectury.fluid.FluidStack;
import me.shedaniel.architectury.registry.Registries;
import me.shedaniel.architectury.utils.Fraction;
import net.minecraft.core.Registry;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public class BoundFluidStackJS extends FluidStackJS
{
	private final FluidStack fluidStack;

	public BoundFluidStackJS(FluidStack fs)
	{
		fluidStack = fs;
	}

	@Override
	public String getId()
	{
		return Registries.getId(fluidStack.getFluid(), Registry.FLUID_REGISTRY).toString();
	}

	@Override
	public Fluid getFluid()
	{
		return fluidStack.getFluid();
	}

	@Override
	public FluidStack getFluidStack()
	{
		return fluidStack;
	}

	@Override
	public int getAmount()
	{
		return fluidStack.getAmount().intValue();
	}

	@Override
	public void setAmount(int amount)
	{
		fluidStack.setAmount(Fraction.ofWhole(amount));
	}

	@Override
	@Nullable
	public MapJS getNbt()
	{
		return MapJS.of(fluidStack.getTag());
	}

	@Override
	public void setNbt(@Nullable Object nbt)
	{
		fluidStack.setTag(MapJS.nbt(nbt));
	}

	@Override
	public FluidStackJS copy()
	{
		return new BoundFluidStackJS(fluidStack.copy());
	}

	@Override
	public void onChanged(@Nullable MapJS o)
	{
		setNbt(o);
	}
}

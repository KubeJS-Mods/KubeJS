package dev.latvian.kubejs.fluid;

import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.WrappedJSObjectChangeListener;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class BoundFluidStackJS extends FluidStackJS implements WrappedJSObjectChangeListener
{
	private final FluidStack fluidStack;

	public BoundFluidStackJS(FluidStack fs)
	{
		fluidStack = fs;
	}

	@Override
	public ResourceLocation getId()
	{
		return fluidStack.getFluid().getRegistryName();
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
		return fluidStack.getAmount();
	}

	@Override
	public void setAmount(int amount)
	{
		fluidStack.setAmount(amount);
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
		fluidStack.setTag(MapJS.nbt(MapJS.of(nbt)));
	}

	@Override
	public FluidStackJS copy()
	{
		return new BoundFluidStackJS(fluidStack.copy());
	}

	@Override
	public void onChanged(@Nullable Object o)
	{
		setNbt(o);
	}
}

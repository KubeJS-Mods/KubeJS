package dev.latvian.kubejs.fluid;

import dev.latvian.kubejs.util.nbt.NBTCompoundJS;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class EmptyFluidStackJS extends FluidStackJS
{
	public static final EmptyFluidStackJS INSTANCE = new EmptyFluidStackJS();

	@Override
	public boolean isEmpty()
	{
		return true;
	}

	@Override
	@Nullable
	public Fluid getFluid()
	{
		return null;
	}

	@Nullable
	@Override
	public FluidStack getFluidStack()
	{
		return null;
	}

	@Override
	public int getAmount()
	{
		return 0;
	}

	@Override
	public void setAmount(int amount)
	{
	}

	@Override
	public NBTCompoundJS getNbt()
	{
		return NBTCompoundJS.NULL;
	}

	@Override
	public void setNbt(@Nullable Object nbt)
	{
	}

	@Override
	public FluidStackJS copy()
	{
		return this;
	}

	@Override
	public boolean equals(Object o)
	{
		return FluidStackJS.of(o).isEmpty();
	}

	@Override
	public boolean strongEquals(Object o)
	{
		return FluidStackJS.of(o).isEmpty();
	}
}
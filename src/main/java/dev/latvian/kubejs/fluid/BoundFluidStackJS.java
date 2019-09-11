package dev.latvian.kubejs.fluid;

import dev.latvian.kubejs.util.nbt.NBTBaseJS;
import dev.latvian.kubejs.util.nbt.NBTCompoundJS;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

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
	public Fluid getFluid()
	{
		return fluidStack.getFluid();
	}

	@Nullable
	@Override
	public FluidStack getFluidStack()
	{
		return fluidStack;
	}

	@Override
	public int getAmount()
	{
		return fluidStack.amount;
	}

	@Override
	public void setAmount(int amount)
	{
		fluidStack.amount = amount;
	}

	@Override
	public NBTCompoundJS getNbt()
	{
		return NBTBaseJS.of(fluidStack.tag).asCompound();
	}

	@Override
	public void setNbt(@Nullable Object nbt)
	{
		fluidStack.tag = NBTBaseJS.of(nbt).asCompound().createNBT();
	}

	@Override
	public FluidStackJS copy()
	{
		return new BoundFluidStackJS(fluidStack.copy());
	}
}

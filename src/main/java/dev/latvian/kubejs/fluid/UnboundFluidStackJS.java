package dev.latvian.kubejs.fluid;

import dev.latvian.kubejs.util.nbt.NBTBaseJS;
import dev.latvian.kubejs.util.nbt.NBTCompoundJS;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class UnboundFluidStackJS extends FluidStackJS
{
	private final Fluid fluid;
	private int amount;
	private NBTCompoundJS nbt;
	private FluidStack cached;

	public UnboundFluidStackJS(Fluid f)
	{
		fluid = f;
		amount = Fluid.BUCKET_VOLUME;
		nbt = NBTCompoundJS.NULL;
		cached = null;
	}

	@Override
	public Fluid getFluid()
	{
		return fluid;
	}

	@Nullable
	@Override
	public FluidStack getFluidStack()
	{
		if (cached == null)
		{
			cached = new FluidStack(fluid, amount, nbt.createNBT());
		}

		return cached;
	}

	@Override
	public int getAmount()
	{
		return amount;
	}

	@Override
	public void setAmount(int a)
	{
		amount = a;
		cached = null;
	}

	@Override
	public NBTCompoundJS getNbt()
	{
		return nbt;
	}

	@Override
	public void setNbt(@Nullable Object n)
	{
		nbt = NBTBaseJS.of(n).asCompound();
		cached = null;
	}

	@Override
	public FluidStackJS copy()
	{
		return new UnboundFluidStackJS(fluid).amount(amount).nbt(nbt.copy());
	}
}
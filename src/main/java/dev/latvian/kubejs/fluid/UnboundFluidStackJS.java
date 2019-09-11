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

	public UnboundFluidStackJS(Fluid f)
	{
		fluid = f;
		amount = Fluid.BUCKET_VOLUME;
		nbt = NBTCompoundJS.NULL;
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
		return new FluidStack(fluid, amount, nbt.createNBT());
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
	}

	@Override
	public FluidStackJS copy()
	{
		return new UnboundFluidStackJS(fluid).amount(amount).nbt(nbt.copy());
	}
}
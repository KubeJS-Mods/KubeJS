package dev.latvian.kubejs.fluid;

import dev.latvian.kubejs.util.nbt.NBTBaseJS;
import dev.latvian.kubejs.util.nbt.NBTCompoundJS;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class UnboundFluidStackJS extends FluidStackJS
{
	private final ResourceLocation fluid;
	private int amount;
	private NBTCompoundJS nbt;
	private FluidStack cached;

	public UnboundFluidStackJS(ResourceLocation f)
	{
		fluid = f;
		amount = FluidAttributes.BUCKET_VOLUME;
		nbt = NBTCompoundJS.NULL;
		cached = null;
	}

	@Override
	public ResourceLocation getFluidID()
	{
		return fluid;
	}

	@Override
	public boolean isEmpty()
	{
		return super.isEmpty() || getFluid() == Fluids.EMPTY;
	}

	@Nullable
	@Override
	public FluidStack getFluidStack()
	{
		if (cached == null)
		{
			Fluid f = getFluid();

			if (f == null)
			{
				return null;
			}

			cached = new FluidStack(f, amount, nbt.createNBT());
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
		return new UnboundFluidStackJS(fluid).amount(amount).nbt(nbt.getCopy());
	}
}
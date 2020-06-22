package dev.latvian.kubejs.fluid;

import dev.latvian.kubejs.util.MapJS;
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
	private final ResourceLocation fluidRL;
	private final String fluid;
	private int amount;
	private MapJS nbt;
	private FluidStack cached;

	public UnboundFluidStackJS(ResourceLocation f)
	{
		fluidRL = f;
		fluid = fluidRL.toString();
		amount = FluidAttributes.BUCKET_VOLUME;
		nbt = null;
		cached = null;
	}

	@Override
	public String getId()
	{
		return fluid;
	}

	@Override
	public boolean isEmpty()
	{
		return super.isEmpty() || getFluid() == Fluids.EMPTY;
	}

	@Override
	public FluidStack getFluidStack()
	{
		if (cached == null)
		{
			cached = new FluidStack(getFluid(), amount, MapJS.nbt(nbt));
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
	@Nullable
	public MapJS getNbt()
	{
		return nbt;
	}

	@Override
	public void setNbt(@Nullable Object n)
	{
		nbt = MapJS.of(n);

		if (nbt != null)
		{
			nbt.changeListener = this;
		}

		cached = null;
	}

	@Override
	public FluidStackJS copy()
	{
		return new UnboundFluidStackJS(fluidRL).amount(amount).nbt(nbt);
	}

	@Override
	public void onChanged(@Nullable MapJS o)
	{
		cached = null;
	}
}
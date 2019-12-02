package dev.latvian.kubejs.fluid;

import dev.latvian.kubejs.util.nbt.NBTBaseJS;
import dev.latvian.kubejs.util.nbt.NBTCompoundJS;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
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
	public ResourceLocation getId()
	{
		return fluidStack.getFluid().getRegistryName();
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
		return fluidStack.getAmount();
	}

	@Override
	public void setAmount(int amount)
	{
		fluidStack.setAmount(amount);
	}

	@Override
	public NBTCompoundJS getNbt()
	{
		return NBTBaseJS.of(fluidStack.getTag()).asCompound();
	}

	@Override
	public void setNbt(@Nullable Object nbt)
	{
		fluidStack.setTag(NBTBaseJS.of(nbt).asCompound().createNBT());
	}

	@Override
	public FluidStackJS copy()
	{
		return new BoundFluidStackJS(fluidStack.copy());
	}
}

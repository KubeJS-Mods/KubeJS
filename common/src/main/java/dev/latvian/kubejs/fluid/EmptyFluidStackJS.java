package dev.latvian.kubejs.fluid;

import dev.latvian.kubejs.util.MapJS;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.Nullable;

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
	public String getId()
	{
		return "minecraft:empty";
	}

	@Override
	public Fluid getFluid()
	{
		return Fluids.EMPTY;
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
	@Nullable
	public MapJS getNbt()
	{
		return null;
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
package dev.latvian.kubejs.fluid;

import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.util.nbt.NBTCompoundJS;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

/**
 * @author LatvianModder
 */
public abstract class FluidStackJS
{
	public static FluidStackJS of(@Nullable Object o)
	{
		if (o == null)
		{
			return EmptyFluidStackJS.INSTANCE;
		}
		else if (o instanceof FluidStackJS)
		{
			return (FluidStackJS) o;
		}
		else if (o instanceof FluidStack)
		{
			return new BoundFluidStackJS((FluidStack) o);
		}
		else if (o instanceof Fluid)
		{
			return new UnboundFluidStackJS(((Fluid) o).getRegistryName());
		}

		Map<String, Object> map = UtilsJS.getNormalizedMap(o);

		if (map != null && map.containsKey("fluid"))
		{
			FluidStackJS stack = new UnboundFluidStackJS(new ResourceLocation(map.get("fluid").toString()));

			if (map.get("amount") instanceof Number)
			{
				stack.setAmount(((Number) map.get("amount")).intValue());
			}

			if (map.containsKey("nbt"))
			{
				stack.setNbt(map.get("nbt"));
			}

			return stack;
		}

		String[] s = o.toString().split(" ", 2);
		return new UnboundFluidStackJS(new ResourceLocation(s[0])).amount(UtilsJS.parseInt(s.length == 2 ? s[1] : "", FluidAttributes.BUCKET_VOLUME));
	}

	public abstract ResourceLocation getFluidID();

	public Fluid getFluid()
	{
		Fluid f = ForgeRegistries.FLUIDS.getValue(getFluidID());
		return f == null ? Fluids.EMPTY : f;
	}

	@Nullable
	public abstract FluidStack getFluidStack();

	public boolean isEmpty()
	{
		return getAmount() <= 0;
	}

	public abstract int getAmount();

	public abstract void setAmount(int amount);

	public final FluidStackJS amount(int amount)
	{
		setAmount(amount);
		return this;
	}

	public abstract NBTCompoundJS getNbt();

	public abstract void setNbt(@Nullable Object nbt);

	public final FluidStackJS nbt(@Nullable Object nbt)
	{
		setNbt(nbt);
		return this;
	}

	public abstract FluidStackJS copy();

	@Override
	public int hashCode()
	{
		return Objects.hash(getFluid(), getNbt());
	}

	public boolean equals(Object o)
	{
		FluidStackJS f = FluidStackJS.of(o);

		if (f.isEmpty())
		{
			return false;
		}

		return getFluid() == f.getFluid() && getNbt().equals(f.getNbt());
	}

	public boolean strongEquals(Object o)
	{
		FluidStackJS f = of(o);

		if (f.isEmpty())
		{
			return false;
		}

		return getAmount() == f.getAmount() && getFluid() == f.getFluid() && getNbt().equals(f.getNbt());
	}

	public String toString()
	{
		NBTCompoundJS out = new NBTCompoundJS();

		if (getFluid() != null)
		{
			out.set("fluid", getFluid().getRegistryName().toString());

			if (getAmount() != FluidAttributes.BUCKET_VOLUME)
			{
				out.set("amount", getAmount());
			}

			if (!getNbt().isNull())
			{
				out.set("nbt", getNbt());
			}
		}
		else
		{
			out.set("fluid", "null");
		}

		return out.toString();
	}
}
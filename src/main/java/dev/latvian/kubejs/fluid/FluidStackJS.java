package dev.latvian.kubejs.fluid;

import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.util.nbt.NBTCompoundJS;
import jdk.nashorn.api.scripting.JSObject;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
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
			return new UnboundFluidStackJS(((Fluid) o).getName());
		}
		else if (o instanceof JSObject)
		{
			JSObject js = (JSObject) o;

			if (js.hasMember("fluid"))
			{
				FluidStackJS stack = new UnboundFluidStackJS(js.getMember("fluid").toString());

				if (js.getMember("amount") instanceof Number)
				{
					stack.setAmount(((Number) js.getMember("amount")).intValue());
				}

				if (js.hasMember("nbt"))
				{
					stack.setNbt(js.getMember("nbt"));
				}

				return stack;
			}
		}

		String[] s = o.toString().split(" ", 2);
		return new UnboundFluidStackJS(s[0]).amount(UtilsJS.parseInt(s.length == 2 ? s[1] : "", Fluid.BUCKET_VOLUME));
	}

	public abstract String getFluidName();

	@Nullable
	public Fluid getFluid()
	{
		return FluidRegistry.getFluid(getFluidName());
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
			out.set("fluid", getFluid().getName());

			if (getAmount() != Fluid.BUCKET_VOLUME)
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
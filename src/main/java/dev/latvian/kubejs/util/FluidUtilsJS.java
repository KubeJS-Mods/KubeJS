package dev.latvian.kubejs.util;

import dev.latvian.kubejs.util.nbt.NBTBaseJS;
import jdk.nashorn.api.scripting.JSObject;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public enum FluidUtilsJS
{
	INSTANCE;

	@Nullable
	public FluidStack copy(@Nullable Object fluid)
	{
		FluidStack stack = of(fluid);
		return stack == null ? null : stack.copy();
	}

	@Nullable
	public FluidStack of(@Nullable Object o)
	{
		if (o == null)
		{
			return null;
		}
		else if (o instanceof FluidStack)
		{
			return (FluidStack) o;
		}
		else if (o instanceof CharSequence)
		{
			Fluid fluid = FluidRegistry.getFluid(o.toString());
			return fluid == null ? null : new FluidStack(fluid, Fluid.BUCKET_VOLUME);
		}
		else if (o instanceof Fluid)
		{
			return new FluidStack((Fluid) o, Fluid.BUCKET_VOLUME);
		}
		else if (o instanceof JSObject)
		{
			JSObject js = (JSObject) o;

			if (js.hasMember("fluid"))
			{
				Fluid fluid = FluidRegistry.getFluid(js.getMember("fluid").toString());

				if (fluid != null)
				{
					FluidStack stack = new FluidStack(fluid, Fluid.BUCKET_VOLUME);

					if (js.getMember("amount") instanceof Number)
					{
						stack.amount = ((Number) js.getMember("amount")).intValue();
					}

					if (js.hasMember("nbt"))
					{
						stack.tag = NBTBaseJS.of(js.getMember("nbt")).asCompound().createNBT();
					}

					return stack;
				}

				return null;
			}
		}

		return null;
	}
}
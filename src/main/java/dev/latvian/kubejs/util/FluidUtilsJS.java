package dev.latvian.kubejs.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
	public FluidStack of(@Nullable Object object, int amount)
	{
		if (object == null)
		{
			return null;
		}
		else if (object instanceof FluidStack)
		{
			FluidStack stack = ((FluidStack) object).copy();
			stack.amount = amount;
			return stack;
		}
		else if (object instanceof CharSequence)
		{
			Fluid fluid = FluidRegistry.getFluid(object.toString());
			return fluid == null ? null : new FluidStack(fluid, amount);
		}
		else if (object instanceof Fluid)
		{
			return new FluidStack((Fluid) object, amount);
		}

		JsonElement e = JsonUtilsJS.INSTANCE.of(object);

		if (e.isJsonObject())
		{
			JsonObject o = e.getAsJsonObject();
			String id = o.has("fluid") ? o.get("fluid").getAsString() : "";

			if (id.isEmpty())
			{
				return null;
			}

			Fluid fluid = FluidRegistry.getFluid(object.toString());
			return fluid == null ? null : new FluidStack(fluid, o.has("amount") ? o.get("amount").getAsInt() : amount);
		}

		return null;
	}

	@Nullable
	public FluidStack of(@Nullable Object object)
	{
		return of(object, Fluid.BUCKET_VOLUME);
	}
}
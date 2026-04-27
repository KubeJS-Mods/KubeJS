package dev.latvian.mods.kubejs.core;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.FluidMatch;
import dev.latvian.mods.kubejs.util.WithCodec;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.Objects;
import java.util.function.Predicate;

@RemapPrefixForJS("kjs$")
public interface FluidIngredientKJS extends WithCodec, FluidMatch, Predicate<FluidStack> {
	default FluidIngredient kjs$self() {
		throw new NoMixinException();
	}

	@Override
	default Codec<?> getCodec(Context cx) {
		return FluidIngredient.CODEC;
	}

	default SizedFluidIngredient kjs$withAmount(int amount) {
		return new SizedFluidIngredient(kjs$self(), amount);
	}

	@Override
	default boolean matches(RecipeMatchContext cx, FluidStack fluid, boolean exact) {
		if (fluid.isEmpty()) {
			return false;
		} else if (exact) {
			var fluids = kjs$self().fluids();
			return fluids.size() == 1 && fluid.is(fluids.getFirst());
		} else {
			return test(fluid);
		}
	}

	@Override
	default boolean matches(RecipeMatchContext cx, FluidIngredient in, boolean exact) {
		if (exact) {
			return Objects.equals(kjs$self(), in);
		}

		try {
			var fluids = kjs$self().fluids();

			if (fluids.isEmpty()) {
				return false;
			}

			int probeAmount = FluidType.BUCKET_VOLUME;
			for (var holder : fluids) {
				if (in.test(new FluidStack(holder, probeAmount))) {
					return true;
				}
			}
		} catch (Exception ex) {
			throw new KubeRuntimeException("Failed to test fluid ingredient " + in, ex);
		}
		return false;
	}

	@Override
	default boolean matches(RecipeMatchContext cx, SizedFluidIngredient in, boolean exact) {
		return matches(cx, in.ingredient(), exact);
	}
}

package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.fluid.FluidLike;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.rhino.Context;
import net.neoforged.neoforge.fluids.FluidStack;

public interface FluidStackKJS extends OutputReplacement, FluidLike {
	default FluidStack kjs$self() {
		return (FluidStack) (Object) this;
	}

	@Override
	default long kjs$getAmount() {
		return kjs$self().getAmount();
	}

	@Override
	default boolean kjs$isEmpty() {
		return kjs$self().isEmpty();
	}

	@Override
	default FluidLike kjs$copy(long amount) {
		return (FluidLike) (Object) kjs$self().copyWithAmount((int) amount);
	}

	@Override
	default Object replaceOutput(Context cx, KubeRecipe recipe, ReplacementMatch match, OutputReplacement original) {
		if (original instanceof FluidLike o) {
			return kjs$copy(o.kjs$getAmount());
		}

		return kjs$copy(kjs$getAmount());
	}
}

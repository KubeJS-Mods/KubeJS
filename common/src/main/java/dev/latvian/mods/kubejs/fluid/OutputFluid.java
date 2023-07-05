package dev.latvian.mods.kubejs.fluid;

import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;

public interface OutputFluid extends FluidLike, OutputReplacement {
	@Override
	default Object replaceOutput(RecipeJS recipe, ReplacementMatch match, OutputReplacement original) {
		if (original instanceof FluidLike o) {
			copy(o.getAmount());
		}

		return copy(getAmount());
	}
}

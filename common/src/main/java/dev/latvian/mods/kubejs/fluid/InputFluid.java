package dev.latvian.mods.kubejs.fluid;

import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;

public interface InputFluid extends FluidLike, InputReplacement {
	@Override
	default Object replaceInput(RecipeJS recipe, ReplacementMatch match, InputReplacement original) {
		if (original instanceof FluidLike o) {
			copy(o.getAmount());
		}

		return copy(getAmount());
	}
}

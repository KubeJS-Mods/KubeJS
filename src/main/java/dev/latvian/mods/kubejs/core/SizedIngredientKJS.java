package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.rhino.Context;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

public interface SizedIngredientKJS extends InputReplacement {
	default SizedIngredient kjs$self() {
		return (SizedIngredient) (Object) this;
	}

	@Override
	default Object replaceInput(Context cx, KubeRecipe recipe, ReplacementMatch match, InputReplacement original) {
		if ((Object) original instanceof SizedIngredient o) {
			return new SizedIngredient(kjs$self().ingredient(), o.count());
		}

		return this;
	}
}

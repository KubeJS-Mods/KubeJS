package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.item.ChancedIngredient;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.rhino.Context;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

public interface SizedIngredientKJS extends InputReplacement, IngredientSupplierKJS {
	default SizedIngredient kjs$self() {
		return (SizedIngredient) (Object) this;
	}

	@Override
	default Object replaceInput(Context cx, KubeRecipe recipe, ReplacementMatch match, InputReplacement original) {
		if (original instanceof SizedIngredientKJS o) {
			return new SizedIngredient(kjs$self().ingredient(), o.kjs$self().count());
		} else if (original instanceof Ingredient) {
			return kjs$self().ingredient();
		}

		return this;
	}

	default ChancedIngredient kjs$withChance(FloatProvider chance) {
		return new ChancedIngredient(kjs$self().ingredient(), kjs$self().count(), chance);
	}

	@Override
	default Ingredient kjs$asIngredient() {
		return kjs$self().ingredient();
	}
}

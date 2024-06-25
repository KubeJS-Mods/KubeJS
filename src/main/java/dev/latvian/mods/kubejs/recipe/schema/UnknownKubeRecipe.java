package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.match.ItemMatch;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.rhino.Context;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class UnknownKubeRecipe extends KubeRecipe {
	public static final KubeRecipeFactory RECIPE_FACTORY = new KubeRecipeFactory(KubeJS.id("unknown"), UnknownKubeRecipe.class, UnknownKubeRecipe::new);

	@Override
	public void deserialize(boolean merge) {
	}

	@Override
	public void serialize() {
	}

	@Override
	public boolean hasInput(Context cx, ReplacementMatchInfo match) {
		if (CommonProperties.get().matchJsonRecipes && match.match() instanceof ItemMatch m && getOriginalRecipe() != null) {
			var arr = getOriginalRecipe().getIngredients();

			//noinspection ConstantValue
			if (arr == null || arr.isEmpty()) {
				return false;
			}

			for (var ingredient : arr) {
				if (ingredient != null && ingredient != Ingredient.EMPTY && ingredient.kjs$canBeUsedForMatching() && m.matches(cx, ingredient, match.exact())) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean replaceInput(Context cx, ReplacementMatchInfo match, Object with) {
		return false;
	}

	@Override
	public boolean hasOutput(Context cx, ReplacementMatchInfo match) {
		if (CommonProperties.get().matchJsonRecipes && match.match() instanceof ItemMatch m && getOriginalRecipe() != null) {
			var result = getOriginalRecipe().getResultItem(type.event.registries.access());
			//noinspection ConstantValue
			return result != null && result != ItemStack.EMPTY && !result.isEmpty() && m.matches(cx, result, match.exact());
		}

		return false;
	}

	@Override
	public boolean replaceOutput(Context cx, ReplacementMatchInfo match, Object with) {
		return false;
	}
}
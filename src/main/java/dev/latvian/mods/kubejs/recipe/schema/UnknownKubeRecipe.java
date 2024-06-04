package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.ItemMatch;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.script.KubeJSContext;
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
	public boolean hasInput(Context cx, ReplacementMatch match) {
		if (CommonProperties.get().matchJsonRecipes && match instanceof ItemMatch m && getOriginalRecipe() != null) {
			var arr = getOriginalRecipe().getIngredients();

			//noinspection ConstantValue
			if (arr == null || arr.isEmpty()) {
				return false;
			}

			for (var ingredient : arr) {
				if (ingredient != null && ingredient != Ingredient.EMPTY && ingredient.kjs$canBeUsedForMatching() && m.contains(ingredient)) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean replaceInput(Context cx, ReplacementMatch match, InputReplacement with) {
		return false;
	}

	@Override
	public boolean hasOutput(Context cx, ReplacementMatch match) {
		if (CommonProperties.get().matchJsonRecipes && match instanceof ItemMatch m && getOriginalRecipe() != null) {
			var result = getOriginalRecipe().getResultItem(((KubeJSContext) cx).getRegistries());
			//noinspection ConstantValue
			return result != null && result != ItemStack.EMPTY && !result.isEmpty() && m.contains(result);
		}

		return false;
	}

	@Override
	public boolean replaceOutput(Context cx, ReplacementMatch match, OutputReplacement with) {
		return false;
	}
}
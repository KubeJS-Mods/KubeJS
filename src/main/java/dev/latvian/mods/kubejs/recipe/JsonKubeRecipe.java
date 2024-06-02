package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.rhino.Context;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class JsonKubeRecipe extends KubeRecipe {
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
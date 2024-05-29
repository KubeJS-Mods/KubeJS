package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.CommonProperties;
import net.minecraft.core.HolderLookup;
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
	public boolean hasInput(HolderLookup.Provider registries, ReplacementMatch match) {
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
	public boolean replaceInput(ReplacementMatch match, InputReplacement with) {
		return false;
	}

	@Override
	public boolean hasOutput(HolderLookup.Provider registries, ReplacementMatch match) {
		if (CommonProperties.get().matchJsonRecipes && match instanceof ItemMatch m && getOriginalRecipe() != null) {
			var result = getOriginalRecipe().getResultItem(registries);
			//noinspection ConstantValue
			return result != null && result != ItemStack.EMPTY && !result.isEmpty() && m.contains(result);
		}

		return false;
	}

	@Override
	public boolean replaceOutput(ReplacementMatch match, OutputReplacement with) {
		return false;
	}
}
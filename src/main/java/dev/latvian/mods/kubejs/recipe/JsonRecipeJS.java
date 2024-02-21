package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public class JsonRecipeJS extends RecipeJS {
	@Override
	public void deserialize(boolean merge) {
	}

	@Override
	public void serialize() {
	}

	@Override
	public boolean hasInput(ReplacementMatch match) {
		if (CommonProperties.get().matchJsonRecipes && match instanceof ItemMatch m && getOriginalRecipe() != null) {
			for (var ingredient : getOriginalRecipe().getIngredients()) {
				if (ingredient != Ingredient.EMPTY && ingredient.kjs$canBeUsedForMatching() && m.contains(ingredient)) {
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
	@SuppressWarnings("ConstantValue")
	public boolean hasOutput(ReplacementMatch match) {
		if (CommonProperties.get().matchJsonRecipes && match instanceof ItemMatch m && getOriginalRecipe() != null) {
			var result = ((Recipe<?>) this).getResultItem(UtilsJS.staticRegistryAccess);
			return result != null && result != ItemStack.EMPTY && !result.isEmpty() && m.contains(result);
		}

		return false;
	}

	@Override
	public boolean replaceOutput(ReplacementMatch match, OutputReplacement with) {
		return false;
	}
}
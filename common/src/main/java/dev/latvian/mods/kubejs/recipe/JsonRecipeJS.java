package dev.latvian.mods.kubejs.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * @author LatvianModder
 */
public class JsonRecipeJS extends RecipeJS {
	public JsonRecipeJS() {
	}

	@Override
	public void create(RecipeArguments args) {
		throw new RecipeExceptionJS("Can't create custom recipe for type " + getOrCreateId() + "!");
	}

	@Override
	public void deserialize() {
	}

	@Override
	public void serialize() {
	}

	@Override
	public boolean hasInput(IngredientMatch match) {
		if (originalRecipe != null) {
			for (Ingredient ingredient : originalRecipe.getIngredients()) {
				if (match.contains(ingredient)) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean replaceInput(IngredientMatch match, Ingredient with, ItemInputTransformer transformer) {
		return false;
	}

	@Override
	public boolean hasOutput(IngredientMatch match) {
		if (originalRecipe != null) {
			return match.contains(originalRecipe.getResultItem());
		}

		return false;
	}

	@Override
	public boolean replaceOutput(IngredientMatch match, ItemStack with, ItemOutputTransformer transformer) {
		return false;
	}
}
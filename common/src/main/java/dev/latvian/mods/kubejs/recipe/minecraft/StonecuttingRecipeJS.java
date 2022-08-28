package dev.latvian.mods.kubejs.recipe.minecraft;

import dev.latvian.mods.kubejs.recipe.IngredientMatch;
import dev.latvian.mods.kubejs.recipe.ItemInputTransformer;
import dev.latvian.mods.kubejs.recipe.ItemOutputTransformer;
import dev.latvian.mods.kubejs.recipe.RecipeArguments;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * @author LatvianModder
 */
public class StonecuttingRecipeJS extends RecipeJS {
	public Ingredient ingredient;
	public ItemStack result;

	@Override
	public void create(RecipeArguments args) {
		result = parseItemOutput(args.get(0));
		ingredient = parseItemInput(args.get(1));
	}

	@Override
	public void deserialize() {
		result = parseItemOutput(json.get("result"));

		if (json.has("count")) {
			result.setCount(json.get("count").getAsInt());
		}

		ingredient = parseItemInput(json.get("ingredient"));
	}

	@Override
	public void serialize() {
		if (serializeOutputs) {
			json.addProperty("result", result.kjs$getId());
			json.addProperty("count", result.getCount());
		}

		if (serializeInputs) {
			json.add("ingredient", ingredient.toJson());
		}
	}

	@Override
	public boolean hasInput(IngredientMatch match) {
		return match.contains(ingredient);
	}

	@Override
	public boolean replaceInput(IngredientMatch match, Ingredient with, ItemInputTransformer transformer) {
		if (match.contains(ingredient)) {
			ingredient = transformer.transform(this, match, ingredient, with);
			return true;
		}

		return false;
	}

	@Override
	public boolean hasOutput(IngredientMatch match) {
		return match.contains(result);
	}

	@Override
	public boolean replaceOutput(IngredientMatch match, ItemStack with, ItemOutputTransformer transformer) {
		if (match.contains(result)) {
			result = transformer.transform(this, match, result, with);
			return true;
		}

		return false;
	}
}
package dev.latvian.mods.kubejs.recipe.minecraft;

import dev.latvian.mods.kubejs.recipe.RecipeArguments;
import dev.latvian.mods.kubejs.recipe.RecipeJS;

/**
 * @author LatvianModder
 */
public class StonecuttingRecipeJS extends RecipeJS {
	@Override
	public void create(RecipeArguments args) {
		outputItems.add(parseResultItem(args.get(0)));
		inputItems.add(parseIngredientItem(args.get(1)));
	}

	@Override
	public void deserialize() {
		var result = parseResultItem(json.get("result"));

		if (json.has("count")) {
			result.setCount(json.get("count").getAsInt());
		}

		outputItems.add(result);
		inputItems.add(parseIngredientItem(json.get("ingredient")));
	}

	@Override
	public void serialize() {
		if (serializeOutputs) {
			json.addProperty("result", outputItems.get(0).getId());
			json.addProperty("count", outputItems.get(0).getCount());
		}

		if (serializeInputs) {
			json.add("ingredient", inputItems.get(0).toJson());
		}
	}
}
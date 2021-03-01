package dev.latvian.kubejs.recipe.minecraft;

import dev.latvian.kubejs.recipe.RecipeJS;
import dev.latvian.kubejs.util.ListJS;

/**
 * @author LatvianModder
 */
public class SmithingRecipeJS extends RecipeJS {
	@Override
	public void create(ListJS args) {
		outputItems.add(parseResultItem(args.get(0)));
		inputItems.add(parseIngredientItem(args.get(1)));
		inputItems.add(parseIngredientItem(args.get(2)));
	}

	@Override
	public void deserialize() {
		outputItems.add(parseResultItem(json.get("result")));
		inputItems.add(parseIngredientItem(json.get("base")));
		inputItems.add(parseIngredientItem(json.get("addition")));
	}

	@Override
	public void serialize() {
		if (serializeOutputs) {
			json.add("result", outputItems.get(0).toResultJson());
		}

		if (serializeInputs) {
			json.add("base", inputItems.get(0).toJson());
			json.add("addition", inputItems.get(1).toJson());
		}
	}
}
package dev.latvian.mods.kubejs.recipe.minecraft;

import com.google.gson.JsonArray;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.util.ListJS;

/**
 * @author LatvianModder
 */
public class ShapelessRecipeJS extends RecipeJS {
	@Override
	public void create(ListJS args) {
		outputItems.add(parseResultItem(args.get(0)));
		inputItems.addAll(parseIngredientItemList(args.get(1)));
	}

	@Override
	public void deserialize() {
		outputItems.add(parseResultItem(json.get("result")));
		inputItems.addAll(parseIngredientItemList(json.get("ingredients")));
	}

	@Override
	public void serialize() {
		if (serializeInputs) {
			var ingredientsJson = new JsonArray();

			for (var in : inputItems) {
				for (var in1 : in.unwrapStackIngredient()) {
					ingredientsJson.add(in1.toJson());
				}
			}

			json.add("ingredients", ingredientsJson);
		}

		if (serializeOutputs) {
			json.add("result", outputItems.get(0).toResultJson());
		}
	}
}
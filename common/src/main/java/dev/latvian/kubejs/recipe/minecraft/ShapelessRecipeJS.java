package dev.latvian.kubejs.recipe.minecraft;

import com.google.gson.JsonArray;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeJS;
import dev.latvian.kubejs.util.ListJS;

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
			JsonArray ingredientsJson = new JsonArray();

			for (IngredientJS in : inputItems) {
				for (IngredientJS in1 : in.unwrapStackIngredient()) {
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
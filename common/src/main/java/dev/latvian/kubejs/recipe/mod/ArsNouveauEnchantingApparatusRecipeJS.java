package dev.latvian.kubejs.recipe.mod;

import dev.latvian.kubejs.recipe.RecipeJS;
import dev.latvian.kubejs.util.ListJS;

/**
 * @author LatvianModder
 */
public class ArsNouveauEnchantingApparatusRecipeJS extends RecipeJS {
	@Override
	public void create(ListJS args) {
		outputItems.add(parseResultItem(args.get(0)));
		inputItems.add(parseIngredientItem(args.get(1)));
		inputItems.addAll(parseIngredientItemList(args.get(2)));
	}

	@Override
	public void deserialize() {
		outputItems.add(parseResultItem(json.get("output")));
		inputItems.add(parseIngredientItem(json.get("reagent")));

		for (int i = 1; i <= 8; i++) {
			if (json.has("item_" + i)) {
				inputItems.add(parseIngredientItem(json.get("item_" + i)));
			}
		}
	}

	@Override
	public void serialize() {
		if (serializeOutputs) {
			json.add("output", outputItems.get(0).toResultJson());
		}

		if (serializeInputs) {
			json.add("reagent", inputItems.get(0).toJson());

			for (int i = 1; i < inputItems.size(); i++) {
				json.add("item_" + i, inputItems.get(i).toJson());
			}
		}
	}
}
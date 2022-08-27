package dev.latvian.mods.kubejs.recipe.minecraft;

import dev.latvian.mods.kubejs.recipe.RecipeArguments;
import dev.latvian.mods.kubejs.recipe.RecipeJS;

/**
 * @author LatvianModder
 */
public class SmithingRecipeJS extends RecipeJS {
	@Override
	public void create(RecipeArguments args) {
		outputItems.add(parseItemOutput(args.get(0)));
		inputItems.add(parseItemInput(args.get(1)));
		inputItems.add(parseItemInput(args.get(2)));
	}

	@Override
	public void deserialize() {
		outputItems.add(parseItemOutput(json.get("result")));
		inputItems.add(parseItemInput(json.get("base")));
		inputItems.add(parseItemInput(json.get("addition")));
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
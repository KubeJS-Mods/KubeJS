package dev.latvian.mods.kubejs.recipe.minecraft;

import dev.latvian.mods.kubejs.recipe.RecipeArguments;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.component.input.RecipeItemInputContainer;
import dev.latvian.mods.kubejs.recipe.component.output.RecipeItemOutputContainer;

/**
 * @author LatvianModder
 */
public class StonecuttingRecipeJS extends RecipeJS {
	public RecipeItemInputContainer ingredient;
	public RecipeItemOutputContainer result;

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
			json.addProperty("result", result.output.kjs$getId());
			json.addProperty("count", result.getCount());
		}

		if (serializeInputs) {
			json.add("ingredient", inputItems.get(0).toJson());
		}
	}
}
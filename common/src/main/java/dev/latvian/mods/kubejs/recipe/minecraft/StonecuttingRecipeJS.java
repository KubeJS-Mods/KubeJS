package dev.latvian.mods.kubejs.recipe.minecraft;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.IngredientMatch;
import dev.latvian.mods.kubejs.recipe.InputItemTransformer;
import dev.latvian.mods.kubejs.recipe.OutputItemTransformer;
import dev.latvian.mods.kubejs.recipe.RecipeArguments;
import dev.latvian.mods.kubejs.recipe.RecipeJS;

/**
 * @author LatvianModder
 */
public class StonecuttingRecipeJS extends RecipeJS {
	public InputItem ingredient;
	public OutputItem result;

	@Override
	public void create(RecipeArguments args) {
		result = parseOutputItem(args.get(0));
		ingredient = parseInputItem(args.get(1));
	}

	@Override
	public void deserialize() {
		result = parseOutputItem(json.get("result"));

		if (json.has("count")) {
			result.item.setCount(json.get("count").getAsInt());
		}

		ingredient = parseInputItem(json.get("ingredient"));
	}

	@Override
	public void serialize() {
		if (serializeOutputs) {
			json.addProperty("result", result.item.kjs$getId());
			json.addProperty("count", result.item.getCount());
		}

		if (serializeInputs) {
			json.add("ingredient", inputToJson(ingredient));
		}
	}

	@Override
	public boolean hasInput(IngredientMatch match) {
		return match.contains(ingredient);
	}

	@Override
	public boolean replaceInput(IngredientMatch match, InputItem with, InputItemTransformer transformer) {
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
	public boolean replaceOutput(IngredientMatch match, OutputItem with, OutputItemTransformer transformer) {
		if (match.contains(result)) {
			result = transformer.transform(this, match, result, with);
			return true;
		}

		return false;
	}
}
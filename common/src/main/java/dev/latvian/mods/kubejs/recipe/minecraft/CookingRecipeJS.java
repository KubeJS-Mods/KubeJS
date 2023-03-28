package dev.latvian.mods.kubejs.recipe.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.architectury.platform.Platform;
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
public class CookingRecipeJS extends RecipeJS {
	public OutputItem result;
	public InputItem ingredient;

	@Override
	public void create(RecipeArguments args) {
		result = parseOutputItem(args.get(0));
		ingredient = parseInputItem(args.get(1));

		if (args.size() >= 3) {
			xp(args.getFloat(2, 0F));
		}

		if (args.size() >= 4) {
			cookingTime(args.getInt(3, 200));
		}
	}

	public CookingRecipeJS xp(float xp) {
		json.addProperty("experience", Math.max(0F, xp));
		save();
		return this;
	}

	public CookingRecipeJS cookingTime(int time) {
		json.addProperty("cookingtime", Math.max(0, time));
		save();
		return this;
	}

	@Override
	public void deserialize() {
		result = parseOutputItem(json.get("result"));
		ingredient = parseInputItem(json.get("ingredient"));
	}

	@Override
	public void serialize() {
		if (serializeOutputs) {
			json.add("result", outputToJson(result));
		}

		if (serializeInputs) {
			json.add("ingredient", inputToJson(ingredient));
		}
	}

	@Override
	public JsonElement outputToJson(OutputItem item) {
		if (Platform.isForge()) {
			return super.outputToJson(result);
		} else {
			return new JsonPrimitive(result.item.kjs$getId());
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
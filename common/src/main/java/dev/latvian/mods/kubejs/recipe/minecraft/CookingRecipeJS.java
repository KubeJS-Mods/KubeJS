package dev.latvian.mods.kubejs.recipe.minecraft;

import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.recipe.RecipeArguments;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.component.input.RecipeItemInputContainer;
import dev.latvian.mods.kubejs.recipe.component.output.RecipeItemOutputContainer;

/**
 * @author LatvianModder
 */
public class CookingRecipeJS extends RecipeJS {
	public RecipeItemOutputContainer result;
	public RecipeItemInputContainer ingredient;

	@Override
	public void create(RecipeArguments args) {
		result = parseItemOutput(args.get(0));
		ingredient = parseItemInput(args.get(1));

		if (args.size() >= 3) {
			xp(args.getFloat(2, 0F));
		}

		if (args.size() >= 4) {
			cookingTime(args.getInt(3, 200));
		}
	}

	@Override
	public void deserialize() {
		result = parseItemOutput(json.get("result"));
		ingredient = parseItemInput(json.get("ingredient"));
	}

	@Override
	public void serialize() {
		if (serializeOutputs) {
			if (Platform.isForge()) {
				json.add("result", result.toJson());
			} else {
				json.addProperty("result", result.getItem().kjs$getId());
			}
		}

		if (serializeInputs) {
			json.add("ingredient", inputItems.get(0).toJson());
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
}
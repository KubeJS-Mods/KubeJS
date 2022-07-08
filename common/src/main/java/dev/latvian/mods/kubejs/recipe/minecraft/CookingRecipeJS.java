package dev.latvian.mods.kubejs.recipe.minecraft;

import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.recipe.RecipeArguments;
import dev.latvian.mods.kubejs.recipe.RecipeJS;

/**
 * @author LatvianModder
 */
public class CookingRecipeJS extends RecipeJS {
	@Override
	public void create(RecipeArguments args) {
		outputItems.add(parseResultItem(args.get(0)));
		inputItems.add(parseIngredientItem(args.get(1)));

		if (args.size() >= 3) {
			xp(((Number) args.get(2)).floatValue());
		}

		if (args.size() >= 4) {
			cookingTime(((Number) args.get(3)).intValue());
		}
	}

	@Override
	public void deserialize() {
		outputItems.add(parseResultItem(json.get("result")));
		inputItems.add(parseIngredientItem(json.get("ingredient")));
	}

	@Override
	public void serialize() {
		if (serializeOutputs) {
			if (Platform.isForge()) {
				json.add("result", outputItems.get(0).toResultJson());
			} else {
				json.addProperty("result", outputItems.get(0).getId());
			}
		}

		if (serializeInputs) {
			json.add("ingredient", inputItems.get(0).toJson());
		}
	}

	public CookingRecipeJS xp(float xp) {
		json.addProperty("experience", Math.max(0F, xp));
		return this;
	}

	public CookingRecipeJS cookingTime(int time) {
		json.addProperty("cookingtime", Math.max(0, time));
		return this;
	}
}
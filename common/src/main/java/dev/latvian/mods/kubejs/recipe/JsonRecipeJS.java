package dev.latvian.mods.kubejs.recipe;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;

public class JsonRecipeJS extends RecipeJS {
	@Override
	public void deserialize(JsonObject json) {
		changed = true;
	}

	@Override
	public void serialize(JsonObject json) {
	}

	public RecipeJS merge(JsonObject j) {
		if (j != null) {
			for (var entry : j.entrySet()) {
				json.add(entry.getKey(), entry.getValue());
			}

			save();
		}

		return this;
	}

	@Override
	public boolean hasInput(IngredientMatch match) {
		if (getOriginalRecipe() != null) {
			for (var ingredient : getOriginalRecipe().getIngredients()) {
				if (match.contains(ingredient)) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean replaceInput(IngredientMatch match, InputItem with, InputItemTransformer transformer) {
		return false;
	}

	@Override
	public boolean hasOutput(IngredientMatch match) {
		if (getOriginalRecipe() != null) {
			return match.contains(getOriginalRecipe().getResultItem());
		}

		return false;
	}

	@Override
	public boolean replaceOutput(IngredientMatch match, OutputItem with, OutputItemTransformer transformer) {
		return false;
	}
}
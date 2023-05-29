package dev.latvian.mods.kubejs.recipe;

import com.google.gson.JsonObject;

public class JsonRecipeJS extends RecipeJS {
	@Override
	public void deserialize() {
	}

	@Override
	public void serialize() {
	}

	@Override
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
	public boolean hasInput(ReplacementMatch match) {
		if (match instanceof ItemMatch m && getOriginalRecipe() != null) {
			for (var ingredient : getOriginalRecipe().getIngredients()) {
				if (m.contains(ingredient)) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean replaceInput(ReplacementMatch match, InputReplacement with) {
		return false;
	}

	@Override
	public boolean hasOutput(ReplacementMatch match) {
		if (match instanceof ItemMatch m && getOriginalRecipe() != null) {
			return m.contains(getOriginalRecipe().getResultItem());
		}

		return false;
	}

	@Override
	public boolean replaceOutput(ReplacementMatch match, OutputReplacement with) {
		return false;
	}
}
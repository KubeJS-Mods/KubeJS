package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;

import java.util.IdentityHashMap;
import java.util.Map;

public class BasicRecipeJS extends RecipeJS {
	public final Map<RecipeKey<?>, Object> values = new IdentityHashMap<>();

	@Override
	public boolean hasInput(IngredientMatch match) {
		return false;
	}

	@Override
	public boolean replaceInput(IngredientMatch match, InputItem with, InputItemTransformer transformer) {
		return false;
	}

	@Override
	public boolean hasOutput(IngredientMatch match) {
		return false;
	}

	@Override
	public boolean replaceOutput(IngredientMatch match, OutputItem with, OutputItemTransformer transformer) {
		return false;
	}
}

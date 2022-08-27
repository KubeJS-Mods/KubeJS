package dev.latvian.mods.kubejs.recipe.minecraft;

import dev.latvian.mods.kubejs.recipe.RecipeArguments;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;

/**
 * @author LatvianModder
 */
public class JsonRecipeJS extends RecipeJS {
	public JsonRecipeJS() {
	}

	@Override
	public void create(RecipeArguments args) {
		throw new RecipeExceptionJS("Can't create custom recipe for type " + getOrCreateId() + "!");
	}

	@Override
	public void deserialize() {
	}

	@Override
	public void serialize() {
	}
}
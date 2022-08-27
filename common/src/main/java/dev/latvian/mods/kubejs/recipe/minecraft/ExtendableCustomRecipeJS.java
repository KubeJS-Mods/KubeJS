package dev.latvian.mods.kubejs.recipe.minecraft;

import dev.latvian.mods.kubejs.recipe.RecipeArguments;

public abstract class ExtendableCustomRecipeJS extends JsonRecipeJS {
	@Override
	public abstract void create(RecipeArguments args);
}

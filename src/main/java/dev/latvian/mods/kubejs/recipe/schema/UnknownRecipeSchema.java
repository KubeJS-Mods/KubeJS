package dev.latvian.mods.kubejs.recipe.schema;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class UnknownRecipeSchema extends RecipeSchema {
	public static final RecipeSchema SCHEMA = new UnknownRecipeSchema().factory(UnknownKubeRecipe.RECIPE_FACTORY);
}

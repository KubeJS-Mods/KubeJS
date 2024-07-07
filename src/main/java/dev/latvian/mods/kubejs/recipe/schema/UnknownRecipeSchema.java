package dev.latvian.mods.kubejs.recipe.schema;

import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Map;

@ApiStatus.Internal
public class UnknownRecipeSchema extends RecipeSchema {
	public static final RecipeSchema SCHEMA = new UnknownRecipeSchema().factory(UnknownKubeRecipe.RECIPE_FACTORY);

	private UnknownRecipeSchema() {
		super(Map.of(), List.of());
	}
}

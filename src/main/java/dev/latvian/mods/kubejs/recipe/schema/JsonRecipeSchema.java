package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.recipe.JsonKubeRecipe;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class JsonRecipeSchema extends RecipeSchema {
	public static final JsonRecipeSchema SCHEMA = new JsonRecipeSchema();

	public JsonRecipeSchema() {
		super(JsonKubeRecipe.class, JsonKubeRecipe::new);
	}
}

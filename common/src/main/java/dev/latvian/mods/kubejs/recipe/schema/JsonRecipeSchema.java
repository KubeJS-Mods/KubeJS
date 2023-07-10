package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.recipe.JsonRecipeJS;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class JsonRecipeSchema extends RecipeSchema {
	public static final JsonRecipeSchema SCHEMA = new JsonRecipeSchema();

	public JsonRecipeSchema() {
		super(JsonRecipeJS.class, JsonRecipeJS::new);
	}
}

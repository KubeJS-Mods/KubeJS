package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.recipe.JsonRecipeJS;

public class JsonRecipeSchema extends RecipeSchema {
	public static final JsonRecipeSchema SCHEMA = new JsonRecipeSchema();

	public JsonRecipeSchema() {
		super(JsonRecipeJS.class, JsonRecipeJS::new);
	}
}

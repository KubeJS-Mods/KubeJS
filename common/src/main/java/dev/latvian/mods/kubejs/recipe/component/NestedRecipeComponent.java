package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.recipe.RecipeJS;

public class NestedRecipeComponent implements RecipeComponent<RecipeJS> {
	public static final RecipeComponent<RecipeJS> RECIPE = new NestedRecipeComponent();
	public static final RecipeComponent<RecipeJS[]> RECIPE_ARRAY = RECIPE.asArray();

	@Override
	public Class<?> componentClass() {
		return RecipeJS.class;
	}

	@Override
	public JsonElement write(RecipeJS recipe, RecipeJS value) {
		recipe.serialize();
		recipe.json.addProperty("type", recipe.type.idString);
		return recipe.json;
	}

	@Override
	public RecipeJS read(RecipeJS recipe, Object from) {
		if (from instanceof RecipeJS r) {
			r.newRecipe = false;
			return r;
		} else if (from instanceof JsonObject json && json.has("type")) {
			var r = recipe.type.event.custom(json);
			r.newRecipe = false;
			return r;
		}

		throw new IllegalArgumentException("Can't parse recipe from " + from);
	}

	@Override
	public boolean hasPriority(RecipeJS recipe, Object from) {
		return from instanceof RecipeJS || from instanceof JsonObject json && json.has("type");
	}
}

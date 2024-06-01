package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.rhino.type.TypeInfo;

public class NestedRecipeComponent implements RecipeComponent<KubeRecipe> {
	public static final RecipeComponent<KubeRecipe> RECIPE = new NestedRecipeComponent();
	public static final RecipeComponent<KubeRecipe[]> RECIPE_ARRAY = RECIPE.asArray();

	@Override
	public TypeInfo typeInfo() {
		return TypeInfo.of(KubeRecipe.class);
	}

	@Override
	public JsonElement write(KubeRecipe recipe, KubeRecipe value) {
		value.serialize();
		value.json.addProperty("type", value.type.idString);
		return value.json;
	}

	@Override
	public KubeRecipe read(KubeRecipe recipe, Object from) {
		if (from instanceof KubeRecipe r) {
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
	public boolean hasPriority(KubeRecipe recipe, Object from) {
		return from instanceof KubeRecipe || from instanceof JsonObject json && json.has("type");
	}
}

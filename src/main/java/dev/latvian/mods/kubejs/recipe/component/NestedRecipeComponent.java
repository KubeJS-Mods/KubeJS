package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;

public class NestedRecipeComponent implements RecipeComponent<KubeRecipe> {
	public static final RecipeComponent<KubeRecipe> RECIPE = new NestedRecipeComponent();

	@Override
	public Codec<KubeRecipe> codec() {
		// FIXME
		throw new UnsupportedOperationException("Nested recipes can't be serialized yet");
	}

	@Override
	public TypeInfo typeInfo() {
		return TypeInfo.of(KubeRecipe.class);
	}

	/*
	@Override
	public JsonElement write(Context cx, KubeRecipe recipe, KubeRecipe value) {
		value.serialize();
		value.json.addProperty("type", value.type.idString);
		return value.json;
	}
	 */

	@Override
	public KubeRecipe wrap(Context cx, KubeRecipe recipe, Object from) {
		if (from instanceof KubeRecipe r) {
			r.newRecipe = false;
			return r;
		} else if (from instanceof JsonObject json && json.has("type")) {
			var r = recipe.type.event.custom(cx, json);
			r.newRecipe = false;
			return r;
		}

		throw new IllegalArgumentException("Can't parse recipe from " + from);
	}

	@Override
	public boolean hasPriority(Context cx, KubeRecipe recipe, Object from) {
		return from instanceof KubeRecipe || from instanceof JsonObject json && json.has("type");
	}

	@Override
	public String toString() {
		return "nested_recipe";
	}
}

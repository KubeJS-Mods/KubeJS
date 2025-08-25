package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.KubeRecipeEventOps;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;

public class NestedRecipeComponent implements RecipeComponent<KubeRecipe> {
	public static final RecipeComponentType<KubeRecipe> RECIPE = RecipeComponentType.unit(KubeJS.id("nested_recipe"), new NestedRecipeComponent());

	@Override
	public RecipeComponentType<?> type() {
		return RECIPE;
	}

	@Override
	public Codec<KubeRecipe> codec() {
		return KubeRecipeEventOps.SYNTHETIC_CODEC;
	}

	@Override
	public TypeInfo typeInfo() {
		return KubeRecipe.TYPE_INFO;
	}

	@Override
	public KubeRecipe wrap(Context cx, KubeRecipe recipe, Object from) {
		if (from instanceof KubeRecipe r) {
			return KubeRecipeEventOps.MARK_SYNTHETIC.apply(r);
		} else if (from instanceof JsonObject json && json.has("type")) {
			return KubeRecipeEventOps.MARK_SYNTHETIC.apply(recipe.type.event.custom(cx, json));
		}

		throw new IllegalArgumentException("Can't parse recipe from " + from);
	}

	@Override
	public boolean hasPriority(Context cx, KubeRecipe recipe, Object from) {
		return from instanceof KubeRecipe || from instanceof JsonObject json && json.has("type");
	}

	@Override
	public String toString() {
		return RECIPE.toString();
	}
}

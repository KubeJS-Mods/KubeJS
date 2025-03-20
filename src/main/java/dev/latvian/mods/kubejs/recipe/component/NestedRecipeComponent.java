package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.KubeRecipeEventOps;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;

import java.util.function.Function;

public class NestedRecipeComponent implements RecipeComponent<KubeRecipe> {
	public static final RecipeComponentType<KubeRecipe> RECIPE = RecipeComponentType.unit(KubeJS.id("nested_recipe"), new NestedRecipeComponent());

	private static final Function<KubeRecipe, KubeRecipe> MARK_SYNTHETIC = r -> {
		r.newRecipe = false;
		return r;
	};

	@Override
	public RecipeComponentType<?> type() {
		return RECIPE;
	}

	@Override
	public Codec<KubeRecipe> codec() {
		return KubeRecipeEventOps.KUBE_RECIPE_CODEC.xmap(MARK_SYNTHETIC, MARK_SYNTHETIC);
	}

	@Override
	public TypeInfo typeInfo() {
		return TypeInfo.of(KubeRecipe.class);
	}

	@Override
	public KubeRecipe wrap(Context cx, KubeRecipe recipe, Object from) {
		if (from instanceof KubeRecipe r) {
			return MARK_SYNTHETIC.apply(r);
		} else if (from instanceof JsonObject json && json.has("type")) {
			return MARK_SYNTHETIC.apply(recipe.type.event.custom(cx, json));
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

package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.KubeRecipeEventOps;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.resources.ResourceKey;
import org.jspecify.annotations.Nullable;

public class NestedRecipeComponent implements RecipeComponent<KubeRecipe> {
	private static final ResourceKey<RecipeComponentType<?>> TYPE = RecipeComponentType.builtin("nested_recipe");

	public static final NestedRecipeComponent RECIPE = new NestedRecipeComponent();

	@Override
	public ResourceKey<RecipeComponentType<?>> type() {
		return TYPE;
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
	public KubeRecipe wrap(RecipeScriptContext cx, @Nullable Object from) {
		if (from instanceof KubeRecipe r) {
			return KubeRecipeEventOps.MARK_SYNTHETIC.apply(r);
		} else if (from instanceof JsonObject json && json.has("type")) {
			return KubeRecipeEventOps.MARK_SYNTHETIC.apply(cx.recipe().type.event.custom(cx.cx(), json));
		}

		throw new IllegalArgumentException("Can't parse recipe from " + from);
	}

	@Override
	public boolean hasPriority(RecipeMatchContext cx, @Nullable Object from) {
		return from instanceof KubeRecipe || from instanceof JsonObject json && json.has("type");
	}

	@Override
	public String toString() {
		return RecipeComponentType.builtin("nested_recipe").toString();
	}
}

package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ItemMatch;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.List;

@RemapPrefixForJS("kjs$")
public interface RecipeHolderKJS extends RecipeLikeKJS {
	default RecipeHolder<?> kjs$self() {
		return (RecipeHolder<?>) this;
	}

	default Recipe<?> kjs$getRecipe() {
		return kjs$self().value();
	}

	@Override
	default String kjs$getGroup() {
		return kjs$getRecipe().group();
	}

	@Override
	default void kjs$setGroup(String group) {
	}

	@Override
	default Identifier kjs$getOrCreateId() {
		return kjs$self().id().identifier();
	}

	@Override
	default RecipeSerializer<?> kjs$getSerializer() {
		return kjs$getRecipe().getSerializer();
	}

	@Override
	default RecipeSchema kjs$getSchema(Context cx) {
		var s = kjs$getType();
		return ((ServerScriptManager) ((KubeJSContext) cx).kjsFactory.manager).recipeSchemaStorage.namespaces.get(s.getNamespace()).get(s.getPath()).schema;
	}

	@Override
	default ResourceKey<RecipeSerializer<?>> kjs$getTypeKey() {
		return BuiltInRegistries.RECIPE_SERIALIZER.getResourceKey(kjs$getSerializer()).orElseThrow();
	}

	@Override
	default boolean hasInput(RecipeMatchContext cx, ReplacementMatchInfo match) {
		if (match.match() instanceof ItemMatch m) {
			var recipe = kjs$getRecipe();
			if (recipe instanceof ShapedRecipe shapedRecipe) {
				for (var in : shapedRecipe.getIngredients()) {
					if (m.matches(cx, in.get(), match.exact())) {
						return true;
					}
				}
			} else if (recipe instanceof ShapelessRecipe shapelessRecipe) {
				for (var in : shapelessRecipe.ingredients) {
					if (m.matches(cx, in, match.exact())) {
						return true;
					}
				}
			}
		}

		return false;
	}

	@Override
	default boolean replaceInput(RecipeScriptContext cx, ReplacementMatchInfo match, Object with) {
		return false;
	}

	@Override
	default boolean hasOutput(RecipeMatchContext cx, ReplacementMatchInfo match) {
		var recipe = kjs$getRecipe();
		if (recipe instanceof ShapedRecipe shaped) {
			if (match.match() instanceof ItemMatch m) {
				var result = shaped.result.create();
				return result != null && result != ItemStack.EMPTY && !result.isEmpty() && m.matches(cx, result, match.exact());
			}
		} else if (recipe instanceof ShapelessRecipe shapeless) {
			if (match.match() instanceof ItemMatch m) {
				var result = shapeless.result.create();
				return result != null && result != ItemStack.EMPTY && !result.isEmpty() && m.matches(cx, result, match.exact());
			}
		}
		return false;
	}

	@Override
	default boolean replaceOutput(RecipeScriptContext cx, ReplacementMatchInfo match, Object with) {
		return false;
	}
}

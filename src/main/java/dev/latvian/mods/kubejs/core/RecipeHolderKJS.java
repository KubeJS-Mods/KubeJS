package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.recipe.match.ItemMatch;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;

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
		return kjs$getRecipe().getGroup();
	}

	@Override
	default void kjs$setGroup(String group) {
	}

	@Override
	default ResourceLocation kjs$getOrCreateId() {
		return kjs$self().id();
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
	default ResourceLocation kjs$getType() {
		return BuiltInRegistries.RECIPE_SERIALIZER.getKey(kjs$getSerializer());
	}

	@Override
	default boolean hasInput(Context cx, ReplacementMatchInfo match) {
		if (match.match() instanceof ItemMatch m) {
			for (var in : kjs$getRecipe().getIngredients()) {
				if (m.matches(cx, in, match.exact())) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	default boolean replaceInput(Context cx, ReplacementMatchInfo match, Object with) {
		return false;
	}

	@Override
	default boolean hasOutput(Context cx, ReplacementMatchInfo match) {
		if (match.match() instanceof ItemMatch m) {
			var result = kjs$getRecipe().getResultItem(RegistryAccessContainer.of(cx).access());
			//noinspection ConstantValue
			return result != null && result != ItemStack.EMPTY && !result.isEmpty() && m.matches(cx, result, match.exact());
		}

		return false;
	}

	@Override
	default boolean replaceOutput(Context cx, ReplacementMatchInfo match, Object with) {
		return false;
	}
}

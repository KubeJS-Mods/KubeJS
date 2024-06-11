package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.ItemMatch;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
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
		return RegistryInfo.RECIPE_SERIALIZER.getId(kjs$getSerializer());
	}

	@Override
	default boolean hasInput(Context cx, ReplacementMatch match) {
		if (match instanceof ItemMatch m) {
			for (var in : kjs$getRecipe().getIngredients()) {
				if (m.contains(in)) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	default boolean replaceInput(Context cx, ReplacementMatch match, InputReplacement with) {
		return false;
	}

	@Override
	default boolean hasOutput(Context cx, ReplacementMatch match) {
		if (match instanceof ItemMatch m) {
			var result = kjs$getRecipe().getResultItem(((KubeJSContext) cx).getRegistries().access());
			//noinspection ConstantValue
			return result != null && result != ItemStack.EMPTY && !result.isEmpty() && m.contains(result);
		}

		return false;
	}

	@Override
	default boolean replaceOutput(Context cx, ReplacementMatch match, OutputReplacement with) {
		return false;
	}
}

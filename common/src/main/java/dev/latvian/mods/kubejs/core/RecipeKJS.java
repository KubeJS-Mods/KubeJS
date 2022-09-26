package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.recipe.IngredientMatch;
import dev.latvian.mods.kubejs.recipe.ItemInputTransformer;
import dev.latvian.mods.kubejs.recipe.ItemOutputTransformer;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

@RemapPrefixForJS("kjs$")
public interface RecipeKJS {
	default String kjs$getGroup() {
		return ((Recipe<?>) this).getGroup();
	}

	default void kjs$setGroup(String group) {
	}

	default ResourceLocation kjs$getOrCreateId() {
		return ((Recipe<?>) this).getId();
	}

	default String kjs$getMod() {
		return kjs$getOrCreateId().getNamespace();
	}

	default ResourceLocation kjs$getType() {
		return KubeJSRegistries.recipeSerializers().getId(((Recipe<?>) this).getSerializer());
	}

	default boolean kjs$hasInput(IngredientMatch match) {
		for (var in : ((Recipe<?>) this).getIngredients()) {
			if (match.contains(in)) {
				return true;
			}
		}

		return false;
	}

	default boolean kjs$replaceInput(IngredientMatch match, Ingredient with, ItemInputTransformer transformer) {
		return false;
	}

	default boolean kjs$hasOutput(IngredientMatch match) {
		return match.contains(((Recipe<?>) this).getResultItem());
	}

	default boolean kjs$replaceOutput(IngredientMatch match, ItemStack with, ItemOutputTransformer transformer) {
		return false;
	}
}

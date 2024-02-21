package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.ItemMatch;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.recipe.schema.RecipeNamespace;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;

@RemapPrefixForJS("kjs$")
public interface RecipeHolderKJS extends RecipeLikeKJS {
	default RecipeHolder<?> kjs$self() {
		return UtilsJS.cast(this);
	}

	default Recipe<?> kjs$getRecipe() {
		return kjs$self().value();
	}

	default String kjs$getGroup() {
		return kjs$getRecipe().getGroup();
	}

	default void kjs$setGroup(String group) {
	}

	default ResourceLocation kjs$getOrCreateId() {
		return kjs$self().id();
	}

	@Override
	default RecipeSerializer<?> kjs$getSerializer() {
		return kjs$getRecipe().getSerializer();
	}

	default RecipeSchema kjs$getSchema() {
		var s = kjs$getType();
		return RecipeNamespace.getAll().get(s.getNamespace()).get(s.getPath()).schema;
	}

	default ResourceLocation kjs$getType() {
		return RegistryInfo.RECIPE_SERIALIZER.getId(kjs$getSerializer());
	}

	default boolean hasInput(ReplacementMatch match) {
		if (match instanceof ItemMatch m) {
			for (var in : kjs$getRecipe().getIngredients()) {
				if (m.contains(in)) {
					return true;
				}
			}
		}

		return false;
	}

	default boolean replaceInput(ReplacementMatch match, InputReplacement with) {
		return false;
	}

	default boolean hasOutput(ReplacementMatch match) {
		if (match instanceof ItemMatch m) {
			var result = kjs$getRecipe().getResultItem(UtilsJS.staticRegistryAccess);
			//noinspection ConstantValue
			return result != null && result != ItemStack.EMPTY && !result.isEmpty() && m.contains(result);
		}

		return false;
	}

	default boolean replaceOutput(ReplacementMatch match, OutputReplacement with) {
		return false;
	}
}

package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

public interface RecipeLikeKJS {
	String kjs$getGroup();

	void kjs$setGroup(String group);

	ResourceLocation kjs$getOrCreateId();

	RecipeSchema kjs$getSchema();

	default String kjs$getMod() {
		return kjs$getOrCreateId().getNamespace();
	}

	ResourceLocation kjs$getType();

	RecipeSerializer<?> kjs$getSerializer();

	default boolean hasInput(ReplacementMatch match) {
		return false;
	}

	default boolean replaceInput(ReplacementMatch match, InputReplacement with) {
		return false;
	}

	default boolean hasOutput(ReplacementMatch match) {
		return false;
	}

	default boolean replaceOutput(ReplacementMatch match, OutputReplacement with) {
		return false;
	}
}

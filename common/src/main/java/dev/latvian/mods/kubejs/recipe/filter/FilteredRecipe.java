package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.recipe.IngredientMatch;
import net.minecraft.resources.ResourceLocation;

public interface FilteredRecipe {
	String getGroup();

	ResourceLocation getOrCreateId();

	String getMod();

	ResourceLocation getType();

	boolean hasInput(IngredientMatch match);

	boolean hasOutput(IngredientMatch match);
}

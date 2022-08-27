package dev.latvian.mods.kubejs.recipe.filter;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

public interface FilteredRecipe {
	String getGroup();

	ResourceLocation getOrCreateId();

	String getMod();

	String getType();

	boolean hasInput(Ingredient in, boolean exact);

	boolean hasOutput(Ingredient out, boolean exact);
}

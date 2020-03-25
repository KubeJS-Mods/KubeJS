package dev.latvian.kubejs.core;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

/**
 * @author LatvianModder
 */
public interface RecipeManagerKJS
{
	void setRecipesKJS(Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> map);
}
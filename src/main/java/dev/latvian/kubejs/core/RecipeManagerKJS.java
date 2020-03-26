package dev.latvian.kubejs.core;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.recipe.RecipeEventJS;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

/**
 * @author LatvianModder
 */
public interface RecipeManagerKJS
{
	void setRecipesKJS(Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> map);

	default void customRecipesKJS(Map<ResourceLocation, JsonObject> jsonMap)
	{
		RecipeEventJS.instance.post((RecipeManager) this, jsonMap);
		RecipeEventJS.instance = null;
	}
}
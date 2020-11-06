package dev.latvian.kubejs.core;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.recipe.RecipeEventJS;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * @author LatvianModder
 */
public interface RecipeManagerKJS
{
	void setRecipesKJS(Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> map);

	default void customRecipesKJS(Map<ResourceLocation, JsonObject> jsonMap)
	{
		if (RecipeEventJS.instance != null)
		{
			RecipeEventJS.instance.post((RecipeManager) this, jsonMap);
			RecipeEventJS.instance = null;
		}
	}
}
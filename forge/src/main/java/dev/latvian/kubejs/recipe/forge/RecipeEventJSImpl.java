package dev.latvian.kubejs.recipe.forge;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.crafting.CraftingHelper;

import java.util.Map;

public class RecipeEventJSImpl
{
	public static boolean processConditions(JsonObject json, String key)
	{
		return CraftingHelper.processConditions(json, key);
	}


	public static void pingNewRecipes(Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> map)
	{
	}
}

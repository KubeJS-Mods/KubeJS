package dev.latvian.kubejs.recipe.forge;

import com.google.gson.JsonObject;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;

import java.util.Map;

public class RecipeEventJSImpl
{
	public static boolean processConditions(JsonObject json, String key)
	{
		return CraftingHelper.processConditions(json, key);
	}


	public static void pingNewRecipes(Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> map)
	{
	}
}

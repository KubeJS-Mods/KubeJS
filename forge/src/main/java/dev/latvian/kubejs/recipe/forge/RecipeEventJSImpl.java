package dev.latvian.kubejs.recipe.forge;

import com.google.gson.JsonObject;
import net.minecraftforge.common.crafting.CraftingHelper;

public class RecipeEventJSImpl
{
	public static boolean processConditions(JsonObject json, String key)
	{
		return CraftingHelper.processConditions(json, key);
	}
}

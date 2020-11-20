package dev.latvian.kubejs.item.ingredient.forge;

import com.google.gson.JsonObject;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;

public class IngredientJSImpl
{
	public static Ingredient getCustomIngredient(JsonObject object)
	{
		return CraftingHelper.getIngredient(object);
	}
}

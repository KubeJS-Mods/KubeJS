package dev.latvian.mods.kubejs.item.ingredient.forge;

import com.google.gson.JsonObject;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;

public class IngredientJSImpl {
	public static Ingredient getCustomIngredient(JsonObject object) {
		return CraftingHelper.getIngredient(object);
	}
}

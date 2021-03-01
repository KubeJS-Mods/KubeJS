package dev.latvian.kubejs.item.ingredient.fabric;

import com.google.gson.JsonObject;
import net.minecraft.world.item.crafting.Ingredient;

public class IngredientJSImpl {
	public static Ingredient getCustomIngredient(JsonObject object) {
		throw new UnsupportedOperationException("Custom ingredients are not present on Fabric!");
	}
}

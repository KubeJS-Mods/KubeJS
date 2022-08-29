package dev.latvian.mods.kubejs.item.ingredient;

import net.minecraft.world.item.crafting.Ingredient;

public interface IngredientStack {
	Ingredient getIngredient();

	int getCount();
}

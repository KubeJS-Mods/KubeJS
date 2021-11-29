package dev.latvian.mods.kubejs.recipe.ingredientaction;

import dev.latvian.mods.kubejs.item.InventoryJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;

@FunctionalInterface
public interface CustomIngredientActionCallback {
	Object transform(ItemStackJS old, int index, InventoryJS craftingTable);
}

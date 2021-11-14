package dev.latvian.kubejs.recipe.ingredientaction;

import dev.latvian.kubejs.item.InventoryJS;
import dev.latvian.kubejs.item.ItemStackJS;

@FunctionalInterface
public interface CustomIngredientActionCallback {
	Object transform(ItemStackJS old, int index, InventoryJS craftingTable);
}

package dev.latvian.mods.kubejs.recipe.ingredientaction;

import dev.latvian.mods.kubejs.core.InventoryKJS;
import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface CustomIngredientActionCallback {
	ItemStack transform(ItemStack old, int index, InventoryKJS craftingTable);
}

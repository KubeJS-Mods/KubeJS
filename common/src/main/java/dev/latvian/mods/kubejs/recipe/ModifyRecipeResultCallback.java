package dev.latvian.mods.kubejs.recipe;

import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface ModifyRecipeResultCallback {
	ItemStack modify(ModifyRecipeCraftingGrid grid, ItemStack result);
}

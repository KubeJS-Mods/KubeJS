package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.item.ItemStackJS;

@FunctionalInterface
public interface ModifyRecipeResultCallback {
	ItemStackJS modify(ModifyRecipeCraftingGrid grid, ItemStackJS result);
}

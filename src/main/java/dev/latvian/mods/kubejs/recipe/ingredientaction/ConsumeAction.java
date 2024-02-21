package dev.latvian.mods.kubejs.recipe.ingredientaction;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

public class ConsumeAction extends IngredientAction {
	@Override
	public ItemStack transform(ItemStack old, int index, CraftingContainer container) {
		return ItemStack.EMPTY;
	}

	@Override
	public String getType() {
		return "consume";
	}
}

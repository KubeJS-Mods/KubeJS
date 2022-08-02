package dev.latvian.mods.kubejs.recipe.ingredientaction;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

public class KeepAction extends IngredientAction {
	@Override
	public ItemStack transform(ItemStack old, int index, CraftingContainer container) {
		old.setCount(1);
		return old;
	}

	@Override
	public String getType() {
		return "keep";
	}
}

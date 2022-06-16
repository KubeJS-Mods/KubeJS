package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import net.minecraft.world.item.ItemStack;

public interface IngredientKJS extends AsKJS<IngredientJS> {
	@Override
	default IngredientJS asKJS() {
		return IngredientJS.of(this);
	}

	ItemStack[] getItemsKJS();
}

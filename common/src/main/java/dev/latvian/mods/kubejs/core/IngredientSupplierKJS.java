package dev.latvian.mods.kubejs.core;

import net.minecraft.world.item.crafting.Ingredient;

public interface IngredientSupplierKJS {
	default Ingredient kjs$asIngredient() {
		throw new NoMixinException();
	}
}

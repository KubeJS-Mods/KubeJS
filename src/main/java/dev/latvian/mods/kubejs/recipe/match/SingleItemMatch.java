package dev.latvian.mods.kubejs.recipe.match;

import dev.latvian.mods.rhino.Context;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public record SingleItemMatch(ItemStack stack) implements ItemMatch {
	@Override
	public boolean matches(Context cx, ItemStack s, boolean exact) {
		return stack.getItem() == s.getItem();
	}

	@Override
	public boolean matches(Context cx, Ingredient in, boolean exact) {
		return in.test(stack);
	}

	@Override
	public boolean matches(Context cx, ItemLike itemLike, boolean exact) {
		return stack.getItem() == itemLike.asItem();
	}

	@Override
	public String toString() {
		return stack.getItem().kjs$getId();
	}
}

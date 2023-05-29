package dev.latvian.mods.kubejs.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public record SingleItemMatch(ItemStack stack) implements ItemMatch {
	@Override
	public boolean contains(ItemStack item) {
		return stack.getItem() == item.getItem();
	}

	@Override
	public boolean contains(Ingredient in) {
		return in.test(stack);
	}

	@Override
	public boolean contains(Block block) {
		return stack.getItem() == block.asItem();
	}

	@Override
	public String toString() {
		return stack.getItem().kjs$getId();
	}
}

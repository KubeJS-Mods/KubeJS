package dev.latvian.mods.kubejs.item.fabric;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ContainerInventoryImpl {
	public static boolean areCapsCompatible(ItemStack a, ItemStack b) {
		return true;
	}

	public static ItemStack getContainerItem(ItemStack stack) {
		var item = stack.getItem();

		if (item.hasCraftingRemainingItem()) {
			return new ItemStack(item.getCraftingRemainingItem());
		}

		return ItemStack.EMPTY;
	}
}

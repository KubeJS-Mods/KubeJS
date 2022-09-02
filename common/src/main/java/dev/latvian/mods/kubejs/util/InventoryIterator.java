package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.kubejs.core.InventoryKJS;
import net.minecraft.world.item.ItemStack;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class InventoryIterator implements Iterator<ItemStack> {
	private final InventoryKJS inventory;
	private int cursor;

	public InventoryIterator(InventoryKJS inventory) {
		this.inventory = inventory;
	}

	@Override
	public boolean hasNext() {
		return cursor < inventory.kjs$getSlots();
	}

	@Override
	public ItemStack next() {
		if (cursor >= inventory.kjs$getSlots()) {
			throw new NoSuchElementException();
		}

		cursor++;
		return inventory.kjs$getStackInSlot(cursor);
	}
}

package dev.latvian.mods.kubejs.item;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

public interface ItemHandler extends Iterable<ItemStack> {
	int getSlots();

	@NotNull
	ItemStack getStackInSlot(int slot);

	@NotNull
	ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate);

	@NotNull
	ItemStack extractItem(int slot, int amount, boolean simulate);

	int getSlotLimit(int slot);

	boolean isItemValid(int slot, @NotNull ItemStack stack);

	@NotNull
	@Override
	default Iterator<ItemStack> iterator() {
		return new Iterator<>() {
			private int cursor;

			@Override
			public boolean hasNext() {
				return cursor < getSlots();
			}

			@Override
			public ItemStack next() {
				int i = cursor;
				if (i >= getSlots()) {
					throw new NoSuchElementException();
				}
				cursor = i + 1;
				return getStackInSlot(i);
			}
		};
	}

	interface Mutable extends ItemHandler {
		void setStackInSlot(int slot, @NotNull ItemStack stack);
	}

	default int getWidth() {
		return getSlots();
	}

	default int getHeight() {
		return 1;
	}
}

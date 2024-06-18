package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.item.ItemPredicate;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@RemapPrefixForJS("kjs$")
public interface InventoryKJS {
	default boolean kjs$isMutable() {
		return false;
	}

	default int kjs$getSlots() {
		throw new NoMixinException();
	}

	default ItemStack kjs$getStackInSlot(int slot) {
		throw new NoMixinException();
	}

	default void kjs$setStackInSlot(int slot, ItemStack stack) {
		throw new IllegalStateException("This item handler can't be modified directly! Use insertItem or extractItem instead!");
	}

	default ItemStack kjs$insertItem(int slot, ItemStack stack, boolean simulate) {
		throw new NoMixinException();
	}

	default ItemStack kjs$extractItem(int slot, int amount, boolean simulate) {
		throw new NoMixinException();
	}

	default ItemStack kjs$insertItem(ItemStack stack, boolean simulate) {
		if (stack.isEmpty()) {
			return stack;
		}

		for (int i = 0; i < kjs$getSlots(); i++) {
			stack = kjs$insertItem(i, stack, simulate);
			if (stack.isEmpty()) {
				return ItemStack.EMPTY;
			}
		}

		return stack;
	}

	default int kjs$getSlotLimit(int slot) {
		throw new NoMixinException();
	}

	default boolean kjs$isItemValid(int slot, ItemStack stack) {
		throw new NoMixinException();
	}

	default int kjs$getWidth() {
		return Math.min(kjs$getSlots(), 9);
	}

	default int kjs$getHeight() {
		return (kjs$getSlots() + 8) / 9;
	}

	default void kjs$clear() {
		for (var i = kjs$getSlots(); i >= 0; i--) {
			if (kjs$isMutable()) {
				kjs$setStackInSlot(i, ItemStack.EMPTY);
			} else {
				kjs$extractItem(i, kjs$getStackInSlot(i).getCount(), false);
			}
		}
	}

	default void kjs$clear(ItemPredicate match) {
		if (match.kjs$isWildcard()) {
			kjs$clear();
		}

		for (var i = kjs$getSlots(); i >= 0; i--) {
			if (match.test(kjs$getStackInSlot(i))) {
				if (kjs$isMutable()) {
					kjs$setStackInSlot(i, ItemStack.EMPTY);
				} else {
					kjs$extractItem(i, kjs$getStackInSlot(i).getCount(), false);
				}
			}
		}
	}

	default int kjs$find() {
		for (var i = 0; i < kjs$getSlots(); i++) {
			var stack1 = kjs$getStackInSlot(i);

			if (!stack1.isEmpty()) {
				return i;
			}
		}

		return -1;
	}

	default int kjs$find(ItemPredicate match) {
		if (match.kjs$isWildcard()) {
			return kjs$find();
		}

		for (var i = 0; i < kjs$getSlots(); i++) {
			var stack1 = kjs$getStackInSlot(i);

			if (match.test(stack1)) {
				return i;
			}
		}

		return -1;
	}

	default int kjs$count() {
		var count = 0;

		for (var i = 0; i < kjs$getSlots(); i++) {
			count += kjs$getStackInSlot(i).getCount();
		}

		return count;
	}

	default int kjs$count(ItemPredicate match) {
		if (match.kjs$isWildcard()) {
			return kjs$count();
		}

		var count = 0;

		for (var i = 0; i < kjs$getSlots(); i++) {
			var stack1 = kjs$getStackInSlot(i);

			if (match.test(stack1)) {
				count += stack1.getCount();
			}
		}

		return count;
	}

	default int kjs$countNonEmpty() {
		var count = 0;

		for (var i = 0; i < kjs$getSlots(); i++) {
			if (!kjs$getStackInSlot(i).isEmpty()) {
				count++;
			}
		}

		return count;
	}

	default int kjs$countNonEmpty(ItemPredicate match) {
		if (match.kjs$isWildcard()) {
			return kjs$countNonEmpty();
		}

		var count = 0;

		for (var i = 0; i < kjs$getSlots(); i++) {
			var stack1 = kjs$getStackInSlot(i);

			if (match.test(stack1)) {
				count++;
			}
		}

		return count;
	}

	default boolean kjs$isEmpty() {
		for (var i = 0; i < kjs$getSlots(); i++) {
			if (!kjs$getStackInSlot(i).isEmpty()) {
				return false;
			}
		}

		return true;
	}

	default void kjs$setChanged() {
	}

	@Nullable
	default BlockContainerJS kjs$getBlock(Level level) {
		return null;
	}

	default List<ItemStack> kjs$getAllItems() {
		var list = new ArrayList<ItemStack>();

		for (var i = 0; i < kjs$getSlots(); i++) {
			ItemStack is = kjs$getStackInSlot(i);

			if (!is.isEmpty()) {
				list.add(is);
			}
		}

		return list;
	}

	default Container kjs$asContainer() {
		return null;
	}
}

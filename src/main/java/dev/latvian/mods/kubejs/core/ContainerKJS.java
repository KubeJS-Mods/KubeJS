package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS("kjs$")
public interface ContainerKJS extends InventoryKJS {
	default Container kjs$self() {
		return (Container) this;
	}

	@Override
	default boolean kjs$isMutable() {
		return true;
	}

	@Override
	default int kjs$getSlots() {
		return this.kjs$self().getContainerSize();
	}

	@Override
	@NotNull
	default ItemStack kjs$getStackInSlot(int slot) {
		return this.kjs$self().getItem(slot);
	}

	@Override
	@NotNull
	default ItemStack kjs$insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		}

		var stackInSlot = this.kjs$self().getItem(slot);

		int m;
		if (!stackInSlot.isEmpty()) {
			if (stackInSlot.getCount() >= Math.min(stackInSlot.getMaxStackSize(), kjs$getSlotLimit(slot))) {
				return stack;
			}

			if (!ItemStack.isSameItemSameComponents(stack, stackInSlot)) {
				return stack;
			}

			if (!this.kjs$self().canPlaceItem(slot, stack)) {
				return stack;
			}

			m = Math.min(stack.getMaxStackSize(), kjs$getSlotLimit(slot)) - stackInSlot.getCount();

			if (stack.getCount() <= m) {
				if (!simulate) {
					var copy = stack.copy();
					copy.grow(stackInSlot.getCount());
					this.kjs$self().setItem(slot, copy);
					this.kjs$self().setChanged();
				}

				return ItemStack.EMPTY;
			} else {
				// copy the stack to not modify the original one
				stack = stack.copy();
				if (!simulate) {
					var copy = stack.split(m);
					copy.grow(stackInSlot.getCount());
					this.kjs$self().setItem(slot, copy);
					this.kjs$self().setChanged();
				} else {
					stack.shrink(m);
				}
				return stack;
			}
		} else {
			if (!this.kjs$self().canPlaceItem(slot, stack)) {
				return stack;
			}

			m = Math.min(stack.getMaxStackSize(), kjs$getSlotLimit(slot));
			if (m < stack.getCount()) {
				// copy the stack to not modify the original one
				stack = stack.copy();
				if (!simulate) {
					this.kjs$self().setItem(slot, stack.split(m));
					this.kjs$self().setChanged();
				} else {
					stack.shrink(m);
				}
				return stack;
			} else {
				if (!simulate) {
					this.kjs$self().setItem(slot, stack);
					this.kjs$self().setChanged();
				}
				return ItemStack.EMPTY;
			}
		}

	}

	@Override
	@NotNull
	default ItemStack kjs$extractItem(int slot, int amount, boolean simulate) {
		if (amount == 0) {
			return ItemStack.EMPTY;
		}

		var stackInSlot = this.kjs$self().getItem(slot);

		if (stackInSlot.isEmpty()) {
			return ItemStack.EMPTY;
		}

		if (simulate) {
			if (stackInSlot.getCount() < amount) {
				return stackInSlot.copy();
			} else {
				var copy = stackInSlot.copy();
				copy.setCount(amount);
				return copy;
			}
		} else {
			var m = Math.min(stackInSlot.getCount(), amount);

			var decrStackSize = this.kjs$self().removeItem(slot, m);
			this.kjs$self().setChanged();
			return decrStackSize;
		}
	}

	@Override
	default void kjs$setStackInSlot(int slot, @NotNull ItemStack stack) {
		this.kjs$self().setItem(slot, stack);
	}

	@Override
	default int kjs$getSlotLimit(int slot) {
		return this.kjs$self().getMaxStackSize();
	}

	@Override
	default boolean kjs$isItemValid(int slot, @NotNull ItemStack stack) {
		return this.kjs$self().canPlaceItem(slot, stack);
	}

	@Override
	default int kjs$getWidth() {
		return kjs$self() instanceof CraftingContainer crafter ? crafter.getWidth() : InventoryKJS.super.kjs$getWidth();
	}

	@Override
	default int kjs$getHeight() {
		return kjs$self() instanceof CraftingContainer crafter ? crafter.getHeight() : InventoryKJS.super.kjs$getHeight();
	}

	@Override
	default void kjs$clear() {
		kjs$self().clearContent();
	}

	@Override
	default void kjs$setChanged() {
		kjs$self().setChanged();

		if (kjs$self() instanceof Inventory inv) {
			inv.player.kjs$sendInventoryUpdate();
		}
	}

	@Override
	default @Nullable LevelBlock kjs$getBlock(Level level) {
		if (kjs$self() instanceof BlockEntity be) {
			return level.kjs$getBlock(be);
		}

		return null;
	}

	@Override
	default Container kjs$asContainer() {
		return kjs$self();
	}
}

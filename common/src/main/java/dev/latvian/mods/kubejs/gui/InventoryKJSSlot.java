package dev.latvian.mods.kubejs.gui;

import dev.latvian.mods.kubejs.core.InventoryKJS;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class InventoryKJSSlot extends Slot {
	public final InventoryKJS inventory;
	public final int index;

	public InventoryKJSSlot(InventoryKJS inventory, int index, int xPosition, int yPosition) {
		super(KubeJSGUI.EMPTY_CONTAINER, index, xPosition, yPosition);
		this.inventory = inventory;
		this.index = index;
	}

	@Override
	public boolean mayPlace(@NotNull ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}
		return inventory.kjs$isItemValid(index, stack);
	}

	@Override
	@NotNull
	public ItemStack getItem() {
		return inventory.kjs$getStackInSlot(index);
	}

	@Override
	public void set(@NotNull ItemStack stack) {
		inventory.kjs$setStackInSlot(index, stack);
		this.setChanged();
	}

	@Override
	public void onQuickCraft(@NotNull ItemStack oldStackIn, @NotNull ItemStack newStackIn) {
	}

	@Override
	public int getMaxStackSize() {
		return this.inventory.kjs$getSlotLimit(this.index);
	}

	@Override
	public int getMaxStackSize(@NotNull ItemStack stack) {
		ItemStack maxAdd = stack.copy();
		int maxInput = stack.getMaxStackSize();
		maxAdd.setCount(maxInput);

		ItemStack currentStack = inventory.kjs$getStackInSlot(index);
		if (inventory.kjs$isMutable()) {
			inventory.kjs$setStackInSlot(index, ItemStack.EMPTY);
			ItemStack remainder = inventory.kjs$insertItem(index, maxAdd, true);
			inventory.kjs$setStackInSlot(index, currentStack);
			return maxInput - remainder.getCount();
		} else {
			ItemStack remainder = inventory.kjs$insertItem(index, maxAdd, true);
			int current = currentStack.getCount();
			int added = maxInput - remainder.getCount();
			return current + added;
		}
	}

	@Override
	public boolean mayPickup(Player playerIn) {
		return !this.inventory.kjs$extractItem(index, 1, true).isEmpty();
	}

	@Override
	@NotNull
	public ItemStack remove(int amount) {
		return this.inventory.kjs$extractItem(index, amount, false);
	}
}

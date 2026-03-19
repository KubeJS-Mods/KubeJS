package dev.latvian.mods.kubejs.gui.chest;

import dev.latvian.mods.kubejs.gui.KubeJSGUI;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ChestMenuContainerSlot extends Slot {
	public final CustomChestMenu menu;
	public final int _index;

	public ChestMenuContainerSlot(CustomChestMenu menu, int index, int xPosition, int yPosition) {
		super(KubeJSGUI.EMPTY_CONTAINER, index, xPosition, yPosition);
		this.menu = menu;
		this._index = index;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return false;
	}

	@Override
	public ItemStack getItem() {
		return menu.data.slots[_index].getItem();
	}

	@Override
	public void set(ItemStack stack) {
		menu.data.slots[_index].setItem(stack);
	}

	@Override
	public void onQuickCraft(ItemStack oldStackIn, ItemStack newStackIn) {
	}

	@Override
	public int getMaxStackSize() {
		return Integer.MAX_VALUE;
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean mayPickup(Player playerIn) {
		return false;
	}

	@Override
	public ItemStack remove(int amount) {
		return ItemStack.EMPTY;
	}
}

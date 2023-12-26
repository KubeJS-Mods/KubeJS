package dev.latvian.mods.kubejs.gui.chest;

import dev.latvian.mods.kubejs.core.InventoryKJS;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChestMenuSlot {
	public final ChestMenuData gui;
	public final int index;
	public final int x;
	public final int y;
	private ItemStack item;
	public int clicked;
	public Map<String, Object> data;
	public final List<ChestMenuClickHandler> clickHandlers;
	public InventoryKJS inventory;
	public int inventorySlot;

	public ChestMenuSlot(ChestMenuData gui, int index) {
		this.gui = gui;
		this.index = index;
		this.x = index % 9;
		this.y = index / 9;
		this.item = ItemStack.EMPTY;
		this.clicked = 0;
		this.data = new HashMap<>();
		this.clickHandlers = new ArrayList<>(1);
		this.inventory = null;
		this.inventorySlot = -1;
	}

	@Override
	public String toString() {
		return "Slot[" + x + "," + y + "]";
	}

	public void setItem(ItemStack stack) {
		if (inventory != null && inventorySlot >= 0) {
			inventory.kjs$setStackInSlot(inventorySlot, stack);
		} else {
			item = stack;
		}
	}

	public ItemStack getItem() {
		if (inventory != null && inventorySlot >= 0) {
			return inventory.kjs$getStackInSlot(inventorySlot);
		}

		return item;
	}

	public void resetClickHandlers() {
		clickHandlers.clear();
	}

	public void clicked(ClickType type, int button, ChestMenuClickCallback callback, boolean autoHandle) {
		clickHandlers.add(new ChestMenuClickHandler(type, button, callback, autoHandle));
	}

	public void setLeftClicked(ChestMenuClickCallback callback) {
		clicked(ClickType.PICKUP, 0, callback, true);
	}

	public void setRightClicked(ChestMenuClickCallback callback) {
		clicked(ClickType.PICKUP, 1, callback, true);
	}

	public void setMiddleClicked(ChestMenuClickCallback callback) {
		clicked(ClickType.CLONE, 2, callback, true);
	}

	public void setSwapped(ChestMenuClickCallback callback) {
		clicked(ClickType.SWAP, -1, callback, true);
	}

	public void setThrown(ChestMenuClickCallback callback) {
		clicked(ClickType.THROW, -1, callback, true);
	}

	public void setShiftLeftClicked(ChestMenuClickCallback callback) {
		clicked(ClickType.QUICK_MOVE, 0, callback, true);
	}

	public void setShiftRightClicked(ChestMenuClickCallback callback) {
		clicked(ClickType.QUICK_MOVE, 1, callback, true);
	}

	public void setDoubleClicked(ChestMenuClickCallback callback) {
		clicked(ClickType.PICKUP_ALL, -1, callback, true);
	}
}
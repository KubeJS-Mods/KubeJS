package dev.latvian.mods.kubejs.gui.chest;

import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class CustomChestMenu extends AbstractContainerMenu {
	public static final MenuType[] TYPES = {
		MenuType.GENERIC_9x1,
		MenuType.GENERIC_9x2,
		MenuType.GENERIC_9x3,
		MenuType.GENERIC_9x4,
		MenuType.GENERIC_9x5,
		MenuType.GENERIC_9x6
	};

	public ChestMenuData data;

	public CustomChestMenu(int containerId, Inventory inventory, ChestMenuData data) {
		super(TYPES[data.rows - 1], containerId);
		this.data = data;

		int k = (data.rows - 4) * 18;

		for (int y = 0; y < data.rows; y++) {
			for (int x = 0; x < 9; x++) {
				addSlot(new ChestMenuContainerSlot(this, x + y * 9, 8 + x * 18, 18 + y * 18));
			}
		}

		if (data.playerSlots) {
			for (int y = 0; y < 3; y++) {
				for (int x = 0; x < 9; x++) {
					addSlot(new Slot(inventory, x + y * 9 + 9, 8 + x * 18, 103 + y * 18 + k));
				}
			}

			for (int x = 0; x < 9; x++) {
				addSlot(new Slot(inventory, x, 8 + x * 18, 161 + k));
			}
		} else {
			for (int y = 0; y < 3; y++) {
				for (int x = 0; x < 9; x++) {
					addSlot(new ChestMenuContainerSlot(this, data.rows * 9 + x + y * 9, 8 + x * 18, 103 + y * 18 + k));
				}
			}

			for (int x = 0; x < 9; x++) {
				addSlot(new ChestMenuContainerSlot(this, data.rows * 9 + 27 + x, 8 + x * 18, 161 + k));
			}
		}
	}

	@Override
	public ItemStack quickMoveStack(Player player, int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public void clicked(int slot, int button, ClickType clickType, Player player) {
		if (data.playerSlots && slot >= data.rows * 9) {
			if (data.inventoryClicked != null && slot >= 0 && slot < slots.size()) {
				data.inventoryClicked.onClick(new ChestMenuInventoryClickEvent(getSlot(slot), clickType, button));
			}

			return;
		}

		if (slot >= data.rows * 9) {
			super.clicked(slot, button, clickType, player);
		}

		try {
			data.handleClick(slot, clickType, button);
		} catch (Exception ex) {
			ConsoleJS.SERVER.error("Error handling chest gui click", ex);
		}

		broadcastFullState();
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public void removed(Player player) {
		if (data.closed != null) {
			data.closed.run();
		}

		player.inventoryMenu.broadcastFullState();
	}

	@Override
	public ItemStack getCarried() {
		return data.mouseItem;
	}

	@Override
	public void setCarried(ItemStack stack) {
		data.mouseItem = stack;
	}

	@Override
	public void initializeContents(int stateId, List<ItemStack> list, ItemStack carried) {
		super.initializeContents(stateId, list, carried);
		data.mouseItem = carried;
	}
}

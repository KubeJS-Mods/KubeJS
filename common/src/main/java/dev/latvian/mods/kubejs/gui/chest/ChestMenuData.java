package dev.latvian.mods.kubejs.gui.chest;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class ChestMenuData {
	public final ServerPlayer player;
	public Component title;
	public final int rows;
	public final ChestMenuSlot[] slots;
	public ChestMenuClickEvent.Callback anyClicked;
	public ChestMenuInventoryClickEvent.Callback inventoryClicked;
	public boolean playerSlots;
	public Runnable closed;
	public ItemStack mouseItem;
	public Container capturedInventory;

	public ChestMenuData(ServerPlayer player, Component title, int rows) {
		this.player = player;
		this.title = title;
		this.rows = rows;
		this.slots = new ChestMenuSlot[9 * (rows + 4)];

		for (int i = 0; i < slots.length; i++) {
			this.slots[i] = new ChestMenuSlot(this, i);
		}

		this.anyClicked = null;
		this.inventoryClicked = null;
		this.playerSlots = false;
		this.closed = null;
		this.mouseItem = ItemStack.EMPTY;
	}

	public ChestMenuSlot getSlot(int x, int y) {
		return slots[x + y * 9];
	}

	public void slot(int x, int y, Consumer<ChestMenuSlot> slot) {
		slot.accept(getSlot(x, y));
	}

	public void slot(int x0, int y0, int x1, int y1, Consumer<ChestMenuSlot> slot) {
		for (int y = y0; y <= y1; y++) {
			for (int x = x0; x <= x1; x++) {
				slot.accept(getSlot(x, y));
			}
		}
	}

	public void button(int x, int y, ItemStack stack, Component displayName, ChestMenuClickEvent.Callback leftClicked) {
		var slot = getSlot(x, y);
		slot.setItem(stack.kjs$withName(displayName));
		slot.setLeftClicked(leftClicked);
	}

	public void handleClick(int index, ClickType type, int button) {
		if (index < 0 || index >= slots.length) {
			return;
		}

		var event = new ChestMenuClickEvent(slots[index], type, button);

		for (var click : event.slot.clickHandlers) {
			if (click.test(event)) {
				click.callback().onClick(event);

				if (event.handled || click.autoHandle()) {
					return;
				}
			}
		}

		if (anyClicked != null) {
			anyClicked.onClick(event);
		}
	}

	public void sync() {
		player.inventoryMenu.broadcastFullState();
	}
}

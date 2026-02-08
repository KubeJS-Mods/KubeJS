package dev.latvian.mods.kubejs.gui.chest;

import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ChestMenuInventoryClickEvent {
	public interface Callback {
		void onClick(ChestMenuInventoryClickEvent event);
	}

	private final Slot slot;
	public final ContainerInput type;
	public final int button;

	public ChestMenuInventoryClickEvent(Slot slot, ContainerInput type, int button) {
		this.slot = slot;
		this.type = type;
		this.button = button;
	}

	public int getIndex() {
		return slot.getContainerSlot();
	}

	public ItemStack getItem() {
		return slot.getItem();
	}

	public void setItem(ItemStack item) {
		slot.set(item);
	}
}

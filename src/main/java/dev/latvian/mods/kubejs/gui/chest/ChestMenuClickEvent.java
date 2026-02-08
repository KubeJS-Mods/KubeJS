package dev.latvian.mods.kubejs.gui.chest;

import net.minecraft.world.inventory.ContainerInput;

public class ChestMenuClickEvent {
	public interface Callback {
		void onClick(ChestMenuClickEvent event);
	}

	public final ChestMenuSlot slot;
	public final ContainerInput type;
	public final int button;
	public transient boolean handled;

	public ChestMenuClickEvent(ChestMenuSlot slot, ContainerInput type, int button) {
		this.slot = slot;
		this.type = type;
		this.button = button;
		this.handled = false;
	}

	public void setHandled() {
		handled = true;
	}
}

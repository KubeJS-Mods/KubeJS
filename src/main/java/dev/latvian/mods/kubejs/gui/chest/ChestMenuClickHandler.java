package dev.latvian.mods.kubejs.gui.chest;

import net.minecraft.world.inventory.ClickType;

public record ChestMenuClickHandler(ClickType type, int button, ChestMenuClickEvent.Callback callback, boolean autoHandle) {
	public boolean test(ChestMenuClickEvent event) {
		return (type == null || type == event.type) && (button == -1 || button == event.button);
	}
}

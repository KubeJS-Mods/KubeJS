package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.entity.EntityJS;
import dev.latvian.mods.kubejs.event.EventHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * @author LatvianModder
 */
public class InventoryEventJS extends PlayerEventJS {
	public static final EventHandler OPENED_EVENT = EventHandler.server(InventoryEventJS.class).name("inventoryOpened").legacy("player.inventory.opened");
	public static final EventHandler CLOSED_EVENT = EventHandler.server(InventoryEventJS.class).name("inventoryClosed").legacy("player.inventory.closed");

	private final Player player;
	private final AbstractContainerMenu menu;

	public InventoryEventJS(Player player, AbstractContainerMenu menu) {
		this.player = player;
		this.menu = menu;
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(player);
	}

	public AbstractContainerMenu getInventoryContainer() {
		return menu;
	}
}
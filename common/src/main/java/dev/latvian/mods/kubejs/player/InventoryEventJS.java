package dev.latvian.mods.kubejs.player;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * @author LatvianModder
 */
public class InventoryEventJS extends PlayerEventJS {
	private final ServerPlayer player;
	private final AbstractContainerMenu menu;

	public InventoryEventJS(ServerPlayer player, AbstractContainerMenu menu) {
		this.player = player;
		this.menu = menu;
	}

	@Override
	public ServerPlayer getEntity() {
		return player;
	}

	public AbstractContainerMenu getInventoryContainer() {
		return menu;
	}
}
package dev.latvian.kubejs.player;

import dev.latvian.kubejs.entity.EntityJS;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * @author LatvianModder
 */
public class InventoryEventJS extends PlayerEventJS {
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
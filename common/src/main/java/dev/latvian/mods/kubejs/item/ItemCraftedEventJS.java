package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.core.InventoryKJS;
import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

/**
 * @author LatvianModder
 */
public class ItemCraftedEventJS extends PlayerEventJS {
	private final ServerPlayer player;
	private final ItemStack crafted;
	private final Container container;

	public ItemCraftedEventJS(ServerPlayer player, ItemStack crafted, Container container) {
		this.player = player;
		this.crafted = crafted;
		this.container = container;
	}

	@Override
	public ServerPlayer getEntity() {
		return player;
	}

	public ItemStack getItem() {
		return crafted;
	}

	public InventoryKJS getInventory() {
		return container;
	}
}
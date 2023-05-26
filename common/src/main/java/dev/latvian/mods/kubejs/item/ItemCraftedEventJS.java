package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.core.InventoryKJS;
import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ItemCraftedEventJS extends PlayerEventJS {
	private final Player player;
	private final ItemStack crafted;
	private final Container container;

	public ItemCraftedEventJS(Player player, ItemStack crafted, Container container) {
		this.player = player;
		this.crafted = crafted;
		this.container = container;
	}

	@Override
	public Player getEntity() {
		return player;
	}

	public ItemStack getItem() {
		return crafted;
	}

	public InventoryKJS getInventory() {
		return container;
	}
}
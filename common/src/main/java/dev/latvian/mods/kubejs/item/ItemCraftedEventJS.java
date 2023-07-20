package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.core.InventoryKJS;
import dev.latvian.mods.kubejs.player.PlayerEventJS;
import dev.latvian.mods.kubejs.typings.JsInfo;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@JsInfo("""
		Invoked when a player crafts an item.
		""")
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
	@JsInfo("The player that crafted the item.")
	public Player getEntity() {
		return player;
	}

	@JsInfo("The item that was crafted.")
	public ItemStack getItem() {
		return crafted;
	}

	@JsInfo("The inventory that the item was crafted in.")
	public InventoryKJS getInventory() {
		return container;
	}
}
package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.core.InventoryKJS;
import dev.latvian.mods.kubejs.player.KubePlayerEvent;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Info("""
	Invoked when a player crafts an item.
	""")
public class ItemCraftedKubeEvent implements KubePlayerEvent {
	private final Player player;
	private final ItemStack crafted;
	private final Container container;

	public ItemCraftedKubeEvent(Player player, ItemStack crafted, Container container) {
		this.player = player;
		this.crafted = crafted;
		this.container = container;
	}

	@Override
	@Info("The player that crafted the item.")
	public Player getEntity() {
		return player;
	}

	@Info("The item that was crafted.")
	public ItemStack getItem() {
		return crafted;
	}

	@Info("The inventory that the item was crafted in.")
	public InventoryKJS getInventory() {
		return container;
	}
}
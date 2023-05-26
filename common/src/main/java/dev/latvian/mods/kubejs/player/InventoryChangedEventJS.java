package dev.latvian.mods.kubejs.player;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class InventoryChangedEventJS extends PlayerEventJS {
	private final Player player;
	private final ItemStack item;
	private final int slot;

	public InventoryChangedEventJS(Player p, ItemStack is, int s) {
		player = p;
		item = is;
		slot = s;
	}

	@Override
	public Player getEntity() {
		return player;
	}

	public ItemStack getItem() {
		return item;
	}

	public int getSlot() {
		return slot;
	}
}
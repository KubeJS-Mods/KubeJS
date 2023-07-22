package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.typings.JsInfo;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@JsInfo("""
		Invoked when a player's inventory changes.
		""")
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
	@JsInfo("Gets the player that changed their inventory.")
	public Player getEntity() {
		return player;
	}

	@JsInfo("Gets the item that was changed.")
	public ItemStack getItem() {
		return item;
	}

	@JsInfo("Gets the slot that was changed.")
	public int getSlot() {
		return slot;
	}
}
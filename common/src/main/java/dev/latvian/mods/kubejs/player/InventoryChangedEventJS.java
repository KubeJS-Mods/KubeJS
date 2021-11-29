package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.entity.EntityJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * @author LatvianModder
 */
public class InventoryChangedEventJS extends PlayerEventJS {
	private final ServerPlayer player;
	private final ItemStack item;
	private final int slot;

	public InventoryChangedEventJS(ServerPlayer p, ItemStack is, int s) {
		player = p;
		item = is;
		slot = s;
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(player);
	}

	public ItemStackJS getItem() {
		return ItemStackJS.of(item);
	}

	public int getSlot() {
		return slot;
	}
}
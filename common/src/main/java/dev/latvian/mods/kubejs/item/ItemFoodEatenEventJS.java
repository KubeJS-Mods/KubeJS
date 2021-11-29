package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.entity.EntityJS;
import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * @author LatvianModder
 */
public class ItemFoodEatenEventJS extends PlayerEventJS {
	private final ServerPlayer player;
	private final ItemStackJS item;

	public ItemFoodEatenEventJS(ServerPlayer p, ItemStack is) {
		player = p;
		item = ItemStackJS.of(is);
	}

	@Override
	public boolean canCancel() {
		return true;
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(player);
	}

	public ItemStackJS getItem() {
		return item;
	}
}
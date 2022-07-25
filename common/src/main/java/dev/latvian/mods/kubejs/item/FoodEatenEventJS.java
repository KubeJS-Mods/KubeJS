package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * @author LatvianModder
 */
public class FoodEatenEventJS extends PlayerEventJS {
	private final ServerPlayer player;
	private final ItemStackJS item;

	public FoodEatenEventJS(ServerPlayer p, ItemStack is) {
		player = p;
		item = ItemStackJS.of(is);
	}

	@Override
	public ServerPlayer getEntity() {
		return player;
	}

	public ItemStackJS getItem() {
		return item;
	}
}
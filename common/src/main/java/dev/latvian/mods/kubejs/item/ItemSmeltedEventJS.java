package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * @author LatvianModder
 */
public class ItemSmeltedEventJS extends PlayerEventJS {
	private final ServerPlayer player;
	private final ItemStack smelted;

	public ItemSmeltedEventJS(ServerPlayer player, ItemStack smelted) {
		this.player = player;
		this.smelted = smelted;
	}

	@Override
	public ServerPlayer getEntity() {
		return player;
	}

	public ItemStack getItem() {
		return smelted;
	}
}
package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

/**
 * @author LatvianModder
 */
public class ItemPickedUpEventJS extends PlayerEventJS {
	private final ServerPlayer player;
	private final ItemEntity entity;
	private final ItemStack stack;

	public ItemPickedUpEventJS(ServerPlayer player, ItemEntity entity, ItemStack stack) {
		this.player = player;
		this.entity = entity;
		this.stack = stack;
	}

	@Override
	public ServerPlayer getEntity() {
		return player;
	}

	public ItemEntity getItemEntity() {
		return entity;
	}

	public ItemStack getItem() {
		return stack;
	}
}
package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

/**
 * @author LatvianModder
 */
public class ItemDroppedEventJS extends PlayerEventJS {
	private final ServerPlayer player;
	private final ItemEntity entity;

	public ItemDroppedEventJS(ServerPlayer player, ItemEntity entity) {
		this.player = player;
		this.entity = entity;
	}

	@Override
	public ServerPlayer getEntity() {
		return player;
	}

	public ItemEntity getItemEntity() {
		return entity;
	}

	public ItemStack getItem() {
		return entity.getItem();
	}
}
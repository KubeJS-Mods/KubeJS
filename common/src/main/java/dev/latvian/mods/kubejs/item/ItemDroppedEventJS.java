package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ItemDroppedEventJS extends PlayerEventJS {
	private final Player player;
	private final ItemEntity entity;

	public ItemDroppedEventJS(Player player, ItemEntity entity) {
		this.player = player;
		this.entity = entity;
	}

	@Override
	public Player getEntity() {
		return player;
	}

	public ItemEntity getItemEntity() {
		return entity;
	}

	public ItemStack getItem() {
		return entity.getItem();
	}
}
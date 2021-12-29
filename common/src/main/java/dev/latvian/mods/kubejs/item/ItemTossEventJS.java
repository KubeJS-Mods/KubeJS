package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.entity.EntityJS;
import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

/**
 * @author LatvianModder
 */
public class ItemTossEventJS extends PlayerEventJS {
	private final Player player;
	private final ItemEntity entity;

	public ItemTossEventJS(Player player, ItemEntity entity) {
		this.player = player;
		this.entity = entity;
	}

	@Override
	public boolean canCancel() {
		return true;
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(player);
	}

	public EntityJS getItemEntity() {
		return getLevel().getEntity(entity);
	}

	public ItemStackJS getItem() {
		return ItemStackJS.of(entity.getItem());
	}
}
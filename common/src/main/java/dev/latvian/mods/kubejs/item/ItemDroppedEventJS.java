package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.entity.EntityJS;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

/**
 * @author LatvianModder
 */
public class ItemDroppedEventJS extends PlayerEventJS {
	public static final EventHandler EVENT = EventHandler.server(ItemDroppedEventJS.class).legacy("item.toss").cancelable();

	private final Player player;
	private final ItemEntity entity;

	public ItemDroppedEventJS(Player player, ItemEntity entity) {
		this.player = player;
		this.entity = entity;
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
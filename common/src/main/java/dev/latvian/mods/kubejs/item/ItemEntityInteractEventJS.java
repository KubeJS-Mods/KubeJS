package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.entity.EntityJS;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

/**
 * @author LatvianModder
 */
public class ItemEntityInteractEventJS extends PlayerEventJS {
	public static final EventHandler EVENT = EventHandler.server(ItemEntityInteractEventJS.class).cancelable().legacy("item.entity_interact");

	private final Player player;
	private final Entity entity;
	private final InteractionHand hand;

	public ItemEntityInteractEventJS(Player player, Entity entity, InteractionHand hand) {

		this.player = player;
		this.entity = entity;
		this.hand = hand;
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(player);
	}

	public InteractionHand getHand() {
		return hand;
	}

	public ItemStackJS getItem() {
		return ItemStackJS.of(player.getItemInHand(hand));
	}

	public EntityJS getTarget() {
		return getLevel().getEntity(entity);
	}
}
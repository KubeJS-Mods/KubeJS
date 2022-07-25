package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

/**
 * @author LatvianModder
 */
public class ItemEntityInteractedEventJS extends PlayerEventJS {
	private final ServerPlayer player;
	private final Entity entity;
	private final InteractionHand hand;

	public ItemEntityInteractedEventJS(ServerPlayer player, Entity entity, InteractionHand hand) {

		this.player = player;
		this.entity = entity;
		this.hand = hand;
	}

	@Override
	public ServerPlayer getEntity() {
		return player;
	}

	public InteractionHand getHand() {
		return hand;
	}

	public ItemStack getItem() {
		return player.getItemInHand(hand);
	}

	public Entity getTarget() {
		return entity;
	}
}
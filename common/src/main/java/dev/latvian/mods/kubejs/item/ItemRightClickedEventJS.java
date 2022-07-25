package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

/**
 * @author LatvianModder
 */
public class ItemRightClickedEventJS extends PlayerEventJS {
	private final ServerPlayer player;
	private final InteractionHand hand;

	public ItemRightClickedEventJS(ServerPlayer player, InteractionHand hand) {
		this.player = player;
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
}
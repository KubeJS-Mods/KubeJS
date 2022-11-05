package dev.latvian.mods.kubejs.player;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

/**
 * @author LatvianModder
 */
public class RightClickeHandEventJS extends PlayerEventJS {
	private final Player player;
	private final InteractionHand hand;

	public RightClickeHandEventJS(Player player, InteractionHand hand) {
		this.player = player;
		this.hand = hand;
	}

	@Override
	public Player getEntity() {
		return player;
	}

	public InteractionHand getHand() {
		return hand;
	}
}
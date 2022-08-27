package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * @author LatvianModder
 */
public class ItemLeftClickedEventJS extends PlayerEventJS {
	private final Player player;
	private final InteractionHand hand;

	public ItemLeftClickedEventJS(Player player, InteractionHand hand) {
		this.player = player;
		this.hand = hand;
	}

	@Override
	public Player getEntity() {
		return player;
	}

	public ItemStack getItem() {
		return ItemStackJS.of(player.getItemInHand(hand));
	}
}
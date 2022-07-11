package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.entity.EntityJS;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

/**
 * @author LatvianModder
 */
public class ItemRightClickedEmptyEventJS extends PlayerEventJS {
	public static final EventHandler EVENT = EventHandler.client(ItemRightClickedEmptyEventJS.class).legacy("item.right_click_empty");

	private final Player player;
	private final InteractionHand hand;

	public ItemRightClickedEmptyEventJS(Player player, InteractionHand hand) {
		this.player = player;
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
		return ItemStackJS.EMPTY;
	}
}
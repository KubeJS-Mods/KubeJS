package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.entity.EntityJS;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.player.PlayerEventJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

/**
 * @author LatvianModder
 */
public class ItemRightClickEventJS extends PlayerEventJS {
	public static final EventHandler EVENT = EventHandler.of(ScriptType.SERVER, ItemRightClickEventJS.class).cancellable();

	private final Player player;
	private final InteractionHand hand;

	public ItemRightClickEventJS(Player player, InteractionHand hand) {
		this.player = player;
		this.hand = hand;
	}

	@Override
	public boolean canCancel() {
		return true;
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
}
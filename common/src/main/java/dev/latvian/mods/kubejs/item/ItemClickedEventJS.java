package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.entity.RayTraceResultJS;
import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * @author LatvianModder
 */
public class ItemClickedEventJS extends PlayerEventJS {
	private final Player player;
	private final InteractionHand hand;
	private RayTraceResultJS target;

	public ItemClickedEventJS(Player player, InteractionHand hand) {
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

	public ItemStack getItem() {
		return player.getItemInHand(hand);
	}

	public RayTraceResultJS getTarget() {
		if (target == null) {
			target = player.kjs$rayTrace();
		}

		return target;
	}
}
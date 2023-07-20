package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import dev.latvian.mods.kubejs.typings.JsInfo;
import dev.latvian.mods.kubejs.typings.JsParam;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@JsInfo(value = """
		Invoked when a player right clicks on an entity.
		""")
public class ItemEntityInteractedEventJS extends PlayerEventJS {
	private final Player player;
	private final Entity entity;
	private final InteractionHand hand;

	public ItemEntityInteractedEventJS(Player player, Entity entity, InteractionHand hand) {

		this.player = player;
		this.entity = entity;
		this.hand = hand;
	}

	@Override
	@JsInfo("The player that interacted with the entity.")
	public Player getEntity() {
		return player;
	}

	@JsInfo("The hand that was used to interact with the entity.")
	public InteractionHand getHand() {
		return hand;
	}

	@JsInfo("The item that was used to interact with the entity.")
	public ItemStack getItem() {
		return player.getItemInHand(hand);
	}

	@JsInfo("The entity that was interacted with.")
	public Entity getTarget() {
		return entity;
	}
}
package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.player.KubePlayerEvent;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Info(value = """
	Invoked when a player right clicks on an entity.
	""")
public class ItemEntityInteractedKubeEvent implements KubePlayerEvent {
	private final Player player;
	private final Entity entity;
	private final InteractionHand hand;
	private final ItemStack item;

	public ItemEntityInteractedKubeEvent(Player player, Entity entity, InteractionHand hand, ItemStack item) {
		this.player = player;
		this.entity = entity;
		this.hand = hand;
		this.item = item;
	}

	@Override
	@Info("The player that interacted with the entity.")
	public Player getEntity() {
		return player;
	}

	@Info("The hand that was used to interact with the entity.")
	public InteractionHand getHand() {
		return hand;
	}

	@Info("The item that was used to interact with the entity.")
	public ItemStack getItem() {
		return item;
	}

	@Info("The entity that was interacted with.")
	public Entity getTarget() {
		return entity;
	}
}
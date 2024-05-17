package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.player.KubePlayerEvent;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Info("""
	Invoked when a player picks up an item. Cancelling (in `ItemEvents.canPickUp`) will prevent the item from being picked up.
	""")
public class ItemPickedUpKubeEvent implements KubePlayerEvent {
	private final Player player;
	private final ItemEntity entity;
	private final ItemStack stack;

	public ItemPickedUpKubeEvent(Player player, ItemEntity entity, ItemStack stack) {
		this.player = player;
		this.entity = entity;
		this.stack = stack;
	}

	@Override
	@Info("The player that picked up the item.")
	public Player getEntity() {
		return player;
	}

	@Info("The item entity that was picked up.")
	public ItemEntity getItemEntity() {
		return entity;
	}

	@Info("The item that was picked up.")
	public ItemStack getItem() {
		return stack;
	}
}
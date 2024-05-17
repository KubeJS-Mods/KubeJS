package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.player.KubePlayerEvent;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Info(value = """
	Invoked when a player drops an item.
	""")
public class ItemDroppedKubeEvent implements KubePlayerEvent {
	private final Player player;
	private final ItemEntity entity;

	public ItemDroppedKubeEvent(Player player, ItemEntity entity) {
		this.player = player;
		this.entity = entity;
	}

	@Override
	@Info("The player that dropped the item.")
	public Player getEntity() {
		return player;
	}

	@Info("The item entity that was spawned when dropping.")
	public ItemEntity getItemEntity() {
		return entity;
	}

	@Info("The item that was dropped.")
	public ItemStack getItem() {
		return entity.getItem();
	}
}
package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import dev.latvian.mods.kubejs.typings.JsInfo;
import dev.latvian.mods.kubejs.typings.JsParam;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@JsInfo(value = """
		Invoked when a player drops an item.
		""")
public class ItemDroppedEventJS extends PlayerEventJS {
	private final Player player;
	private final ItemEntity entity;

	public ItemDroppedEventJS(Player player, ItemEntity entity) {
		this.player = player;
		this.entity = entity;
	}

	@Override
	@JsInfo("The player that dropped the item.")
	public Player getEntity() {
		return player;
	}

	@JsInfo("The item entity that was spawned when dropping.")
	public ItemEntity getItemEntity() {
		return entity;
	}

	@JsInfo("The item that was dropped.")
	public ItemStack getItem() {
		return entity.getItem();
	}
}
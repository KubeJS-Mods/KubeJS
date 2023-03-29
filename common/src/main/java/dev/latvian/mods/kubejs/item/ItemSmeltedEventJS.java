package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * @author LatvianModder
 */
public class ItemSmeltedEventJS extends PlayerEventJS {
	private final Player player;
	private final ItemStack smelted;

	public ItemSmeltedEventJS(Player player, ItemStack smelted) {
		this.player = player;
		this.smelted = smelted;
	}

	@Override
	public Player getEntity() {
		return player;
	}

	public ItemStack getItem() {
		return smelted;
	}
}
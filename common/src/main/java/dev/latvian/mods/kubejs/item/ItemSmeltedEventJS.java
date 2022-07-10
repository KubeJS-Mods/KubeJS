package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.entity.EntityJS;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * @author LatvianModder
 */
public class ItemSmeltedEventJS extends PlayerEventJS {
	public static final EventHandler EVENT = EventHandler.server(ItemSmeltedEventJS.class).legacy("item.smelted");

	private final Player player;
	private final ItemStack smelted;

	public ItemSmeltedEventJS(Player player, ItemStack smelted) {
		this.player = player;
		this.smelted = smelted;
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(player);
	}

	public ItemStackJS getItem() {
		return ItemStackJS.of(smelted);
	}
}
package dev.latvian.kubejs.item;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * @author LatvianModder
 */
public class ItemCraftedEventJS extends PlayerEventJS
{
	public final Player player;
	public final ItemStack crafted;
	public final Container container;

	public ItemCraftedEventJS(Player player, ItemStack crafted, Container container)
	{
		this.player = player;
		this.crafted = crafted;
		this.container = container;
	}

	@Override
	public EntityJS getEntity()
	{
		return entityOf(player);
	}

	public ItemStackJS getItem()
	{
		return ItemStackJS.of(crafted);
	}

	public InventoryJS getInventory()
	{
		return new InventoryJS(container);
	}
}
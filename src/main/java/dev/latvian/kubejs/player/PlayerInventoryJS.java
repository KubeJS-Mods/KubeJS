package dev.latvian.kubejs.player;

import dev.latvian.kubejs.item.InventoryJS;
import dev.latvian.kubejs.item.ItemStackJS;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;

/**
 * @author LatvianModder
 */
public class PlayerInventoryJS extends InventoryJS
{
	public final PlayerJS player;

	public PlayerInventoryJS(PlayerJS p)
	{
		super(new InvWrapper(p.player.inventory));
		player = p;
	}

	public void give(Object item)
	{
		ItemHandlerHelper.giveItemToPlayer(player.player, ItemStackJS.of(item).itemStack());
	}

	public void giveInHand(Object item)
	{
		ItemHandlerHelper.giveItemToPlayer(player.player, ItemStackJS.of(item).itemStack(), selectedSlot());
	}

	public ItemStackJS getEquipment(EntityEquipmentSlot slot)
	{
		return ItemStackJS.of(player.player.getItemStackFromSlot(slot));
	}

	public void setEquipment(EntityEquipmentSlot slot, Object item)
	{
		player.player.setItemStackToSlot(slot, ItemStackJS.of(item).itemStack());
	}

	public int selectedSlot()
	{
		return player.player.inventory.currentItem;
	}
}
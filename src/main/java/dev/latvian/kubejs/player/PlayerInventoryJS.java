package dev.latvian.kubejs.player;

import dev.latvian.kubejs.item.InventoryJS;
import dev.latvian.kubejs.item.ItemStackJS;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.EnumHand;
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
		ItemHandlerHelper.giveItemToPlayer(player.player, ItemStackJS.of(item).itemStack(), getSelectedSlot());
	}

	public ItemStackJS getEquipment(EntityEquipmentSlot slot)
	{
		return ItemStackJS.of(player.player.getItemStackFromSlot(slot));
	}

	public void setEquipment(EntityEquipmentSlot slot, Object item)
	{
		player.player.setItemStackToSlot(slot, ItemStackJS.of(item).itemStack());
	}

	public int getSelectedSlot()
	{
		return player.player.inventory.currentItem;
	}

	public ItemStackJS getHandItem(boolean mainHand)
	{
		return ItemStackJS.of(player.player.getHeldItem(mainHand ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND));
	}
}
package dev.latvian.kubejs.player;

import dev.latvian.kubejs.item.InventoryJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.util.UtilsJS;
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
		ItemHandlerHelper.giveItemToPlayer(player.player, UtilsJS.INSTANCE.item(item).itemStack());
	}

	public void giveInHand(Object item)
	{
		ItemHandlerHelper.giveItemToPlayer(player.player, UtilsJS.INSTANCE.item(item).itemStack(), selectedSlot());
	}

	public ItemStackJS getEquipment(int slot)
	{
		return UtilsJS.INSTANCE.item(player.player.getItemStackFromSlot(UtilsJS.INSTANCE.equipmentSlot(slot)));
	}

	public void setEquipment(int slot, Object item)
	{
		player.player.setItemStackToSlot(UtilsJS.INSTANCE.equipmentSlot(slot), UtilsJS.INSTANCE.item(item).itemStack());
	}

	public int selectedSlot()
	{
		return player.player.inventory.currentItem;
	}
}
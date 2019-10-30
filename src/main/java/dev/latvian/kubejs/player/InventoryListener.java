package dev.latvian.kubejs.player;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.event.EventsJS;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

/**
 * @author LatvianModder
 */
public class InventoryListener implements IContainerListener
{
	public final EntityPlayerMP player;

	public InventoryListener(EntityPlayerMP p)
	{
		player = p;
	}

	@Override
	public void sendAllContents(Container container, NonNullList<ItemStack> itemsList)
	{
		EventsJS.post(KubeJSEvents.PLAYER_INVENTORY_CHANGED, new InventoryChangedEventJS(player, ItemStack.EMPTY, -1));
	}

	@Override
	public void sendSlotContents(Container container, int index, ItemStack stack)
	{
		if (!stack.isEmpty() && container.getSlot(index).inventory == player.inventory)
		{
			EventsJS.post(KubeJSEvents.PLAYER_INVENTORY_CHANGED, new InventoryChangedEventJS(player, stack, index));
		}
	}

	@Override
	public void sendWindowProperty(Container container, int id, int value)
	{
	}

	@Override
	public void sendAllWindowProperties(Container container, IInventory inventory)
	{
	}
}
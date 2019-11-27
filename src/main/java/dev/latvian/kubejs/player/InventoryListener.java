package dev.latvian.kubejs.player;

import dev.latvian.kubejs.KubeJSEvents;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

/**
 * @author LatvianModder
 */
public class InventoryListener implements IContainerListener
{
	public final ServerPlayerEntity player;

	public InventoryListener(ServerPlayerEntity p)
	{
		player = p;
	}

	@Override
	public void sendAllContents(Container container, NonNullList<ItemStack> itemsList)
	{
		new InventoryChangedEventJS(player, ItemStack.EMPTY, -1).post(KubeJSEvents.PLAYER_INVENTORY_CHANGED);
	}

	@Override
	public void sendSlotContents(Container container, int index, ItemStack stack)
	{
		if (!stack.isEmpty() && container.getSlot(index).inventory == player.inventory)
		{
			new InventoryChangedEventJS(player, stack, index).post(KubeJSEvents.PLAYER_INVENTORY_CHANGED);
		}
	}

	@Override
	public void sendWindowProperty(Container container, int id, int value)
	{
	}
}
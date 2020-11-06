package dev.latvian.kubejs.player;

import dev.latvian.kubejs.KubeJSEvents;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;

/**
 * @author LatvianModder
 */
public class InventoryListener implements ContainerListener
{
	public final ServerPlayer player;

	public InventoryListener(ServerPlayer p)
	{
		player = p;
	}

	@Override
	public void refreshContainer(AbstractContainerMenu container, NonNullList<ItemStack> itemsList)
	{
		new InventoryChangedEventJS(player, ItemStack.EMPTY, -1).post(KubeJSEvents.PLAYER_INVENTORY_CHANGED);
	}

	@Override
	public void slotChanged(AbstractContainerMenu container, int index, ItemStack stack)
	{
		if (!stack.isEmpty() && container.getSlot(index).container == player.inventory)
		{
			new InventoryChangedEventJS(player, stack, index).post(KubeJSEvents.PLAYER_INVENTORY_CHANGED);
		}
	}

	@Override
	public void setContainerData(AbstractContainerMenu container, int id, int value)
	{
	}
}
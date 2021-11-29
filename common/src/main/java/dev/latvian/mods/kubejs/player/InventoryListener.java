package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.KubeJSEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;

/**
 * @author LatvianModder
 */
public class InventoryListener implements ContainerListener {
	public final ServerPlayer player;

	public InventoryListener(ServerPlayer p) {
		player = p;
	}

	// TODO: re-add?
	/*
	@Override
	public void refreshContainer(AbstractContainerMenu container, NonNullList<ItemStack> itemsList) {
		new InventoryChangedEventJS(player, ItemStack.EMPTY, -1).post(KubeJSEvents.PLAYER_INVENTORY_CHANGED);
	}
	*/

	@Override
	public void slotChanged(AbstractContainerMenu container, int index, ItemStack stack) {
		if (!stack.isEmpty() && container.getSlot(index).container == player.getInventory()) {
			new InventoryChangedEventJS(player, stack, index).post(KubeJSEvents.PLAYER_INVENTORY_CHANGED);
		}
	}

	@Override
	public void dataChanged(AbstractContainerMenu container, int id, int value) {
	}
}
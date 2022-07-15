package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.KubeJSEvents;
import dev.latvian.mods.kubejs.bindings.ItemWrapper;
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

	@Override
	public void slotChanged(AbstractContainerMenu container, int index, ItemStack stack) {
		if (!stack.isEmpty() && container.getSlot(index).container == player.getInventory()) {
			KubeJSEvents.PLAYER_INVENTORY_CHANGED.post(ItemWrapper.getId(stack.getItem()), new InventoryChangedEventJS(player, stack, index));
		}
	}

	@Override
	public void dataChanged(AbstractContainerMenu container, int id, int value) {
	}
}
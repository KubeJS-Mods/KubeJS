package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.bindings.event.PlayerEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;

public class KubeJSInventoryListener implements ContainerListener {
	public final Player player;

	public KubeJSInventoryListener(Player p) {
		player = p;
	}

	@Override
	public void slotChanged(AbstractContainerMenu container, int index, ItemStack stack) {
		if (PlayerEvents.INVENTORY_CHANGED.hasListeners() && !stack.isEmpty() && container.getSlot(index).container == player.getInventory()) {
			PlayerEvents.INVENTORY_CHANGED.post(player, stack.getItem(), new InventoryChangedKubeEvent(player, stack, index));
		}
	}

	@Override
	public void dataChanged(AbstractContainerMenu container, int id, int value) {
	}
}
/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.core.InventoryKJS;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

public class ItemHandlerUtils {
	public static void giveItemToPlayer(Player player, @NotNull ItemStack stack, int preferredSlot) {
		ItemHandlerHelper.giveItemToPlayer(player, stack, preferredSlot);
	}

	@NotNull
	public static ItemStack insertItemStacked(InventoryKJS inventory, @NotNull ItemStack stack, boolean simulate) {
		if (inventory == null || stack.isEmpty()) {
			return stack;
		}

		// not stackable -> just insert into a new slot
		if (!stack.isStackable()) {
			return insertItem(inventory, stack, simulate);
		}

		var sizeInventory = inventory.kjs$getSlots();

		// go through the inventory and try to fill up already existing items
		for (var i = 0; i < sizeInventory; i++) {
			var slot = inventory.kjs$getStackInSlot(i);
			if (ItemStack.isSameItemSameComponents(slot, stack)) {
				stack = inventory.kjs$insertItem(i, stack, simulate);

				if (stack.isEmpty()) {
					break;
				}
			}
		}

		// insert remainder into empty slots
		if (!stack.isEmpty()) {
			// find empty slot
			for (var i = 0; i < sizeInventory; i++) {
				if (inventory.kjs$getStackInSlot(i).isEmpty()) {
					stack = inventory.kjs$insertItem(i, stack, simulate);
					if (stack.isEmpty()) {
						break;
					}
				}
			}
		}

		return stack;
	}

	@NotNull
	public static ItemStack insertItem(InventoryKJS dest, @NotNull ItemStack stack, boolean simulate) {
		if (dest == null || stack.isEmpty()) {
			return stack;
		}

		for (var i = 0; i < dest.kjs$getSlots(); i++) {
			stack = dest.kjs$insertItem(i, stack, simulate);
			if (stack.isEmpty()) {
				return ItemStack.EMPTY;
			}
		}

		return stack;
	}
}

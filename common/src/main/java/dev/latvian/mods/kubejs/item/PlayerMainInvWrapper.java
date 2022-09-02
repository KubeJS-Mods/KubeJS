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

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerMainInvWrapper extends RangedWrapper {
	private final Inventory inventoryPlayer;

	public PlayerMainInvWrapper(Inventory inv) {
		super(inv, 0, inv.items.size());
		inventoryPlayer = inv;
	}

	@Override
	@NotNull
	public ItemStack kjs$insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		var rest = super.kjs$insertItem(slot, stack, simulate);
		if (rest.getCount() != stack.getCount()) {
			// the stack in the slot changed, animate it
			var inSlot = kjs$getStackInSlot(slot);
			if (!inSlot.isEmpty()) {
				if (getInventoryPlayer().player.level.isClientSide) {
					inSlot.setPopTime(5);
				} else if (getInventoryPlayer().player instanceof ServerPlayer) {
					getInventoryPlayer().player.containerMenu.broadcastChanges();
				}
			}
		}
		return rest;
	}

	public Inventory getInventoryPlayer() {
		return inventoryPlayer;
	}
}
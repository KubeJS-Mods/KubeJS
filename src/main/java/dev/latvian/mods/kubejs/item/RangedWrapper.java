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

import com.google.common.base.Preconditions;
import dev.latvian.mods.kubejs.core.InventoryKJS;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RangedWrapper implements InventoryKJS {

	private final InventoryKJS compose;
	private final int minSlot;
	private final int maxSlot;

	public RangedWrapper(InventoryKJS compose, int minSlot, int maxSlotExclusive) {
		Preconditions.checkArgument(maxSlotExclusive > minSlot, "Max slot must be greater than min slot");
		this.compose = compose;
		this.minSlot = minSlot;
		this.maxSlot = maxSlotExclusive;
	}

	@Override
	public boolean kjs$isMutable() {
		return compose.kjs$isMutable();
	}

	@Override
	public int kjs$getSlots() {
		return maxSlot - minSlot;
	}

	@Override
	@NotNull
	public ItemStack kjs$getStackInSlot(int slot) {
		if (checkSlot(slot)) {
			return compose.kjs$getStackInSlot(slot + minSlot);
		}

		return ItemStack.EMPTY;
	}

	@Override
	@NotNull
	public ItemStack kjs$insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		if (checkSlot(slot)) {
			return compose.kjs$insertItem(slot + minSlot, stack, simulate);
		}

		return stack;
	}

	@Override
	@NotNull
	public ItemStack kjs$extractItem(int slot, int amount, boolean simulate) {
		if (checkSlot(slot)) {
			return compose.kjs$extractItem(slot + minSlot, amount, simulate);
		}

		return ItemStack.EMPTY;
	}

	@Override
	public void kjs$setStackInSlot(int slot, @NotNull ItemStack stack) {
		if (checkSlot(slot)) {
			compose.kjs$setStackInSlot(slot + minSlot, stack);
		}
	}

	@Override
	public int kjs$getSlotLimit(int slot) {
		if (checkSlot(slot)) {
			return compose.kjs$getSlotLimit(slot + minSlot);
		}

		return 0;
	}

	@Override
	public boolean kjs$isItemValid(int slot, @NotNull ItemStack stack) {
		if (checkSlot(slot)) {
			return compose.kjs$isItemValid(slot + minSlot, stack);
		}

		return false;
	}

	private boolean checkSlot(int localSlot) {
		return localSlot + minSlot < maxSlot;
	}
}

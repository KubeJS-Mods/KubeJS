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

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ItemHandlerUtils {
	public static void giveItemToPlayer(Player player, @NotNull ItemStack stack, int preferredSlot) {
		if (stack.isEmpty()) {
			return;
		}

		ItemHandler inventory = new PlayerMainInvWrapper(player.getInventory());
		Level world = player.level;

		// try adding it into the inventory
		ItemStack remainder = stack;
		// insert into preferred slot first
		if (preferredSlot >= 0 && preferredSlot < inventory.getSlots()) {
			remainder = inventory.insertItem(preferredSlot, stack, false);
		}
		// then into the inventory in general
		if (!remainder.isEmpty()) {
			remainder = insertItemStacked(inventory, remainder, false);
		}

		// play sound if something got picked up
		if (remainder.isEmpty() || remainder.getCount() != stack.getCount()) {
			world.playSound(null, player.getX(), player.getY() + 0.5, player.getZ(),
					SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((world.random.nextFloat() - world.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
		}

		// drop remaining itemstack into the world
		if (!remainder.isEmpty() && !world.isClientSide) {
			ItemEntity itemEntity = new ItemEntity(world, player.getX(), player.getY() + 0.5, player.getZ(), remainder);
			itemEntity.setPickUpDelay(40);
			itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().multiply(0, 1, 0));

			world.addFreshEntity(itemEntity);
		}
	}

	@NotNull
	public static ItemStack insertItemStacked(ItemHandler inventory, @NotNull ItemStack stack, boolean simulate) {
		if (inventory == null || stack.isEmpty()) {
			return stack;
		}

		// not stackable -> just insert into a new slot
		if (!stack.isStackable()) {
			return insertItem(inventory, stack, simulate);
		}

		int sizeInventory = inventory.getSlots();

		// go through the inventory and try to fill up already existing items
		for (int i = 0; i < sizeInventory; i++) {
			ItemStack slot = inventory.getStackInSlot(i);
			if (canItemStacksStackRelaxed(slot, stack)) {
				stack = inventory.insertItem(i, stack, simulate);

				if (stack.isEmpty()) {
					break;
				}
			}
		}

		// insert remainder into empty slots
		if (!stack.isEmpty()) {
			// find empty slot
			for (int i = 0; i < sizeInventory; i++) {
				if (inventory.getStackInSlot(i).isEmpty()) {
					stack = inventory.insertItem(i, stack, simulate);
					if (stack.isEmpty()) {
						break;
					}
				}
			}
		}

		return stack;
	}

	@NotNull
	public static ItemStack insertItem(ItemHandler dest, @NotNull ItemStack stack, boolean simulate) {
		if (dest == null || stack.isEmpty()) {
			return stack;
		}

		for (int i = 0; i < dest.getSlots(); i++) {
			stack = dest.insertItem(i, stack, simulate);
			if (stack.isEmpty()) {
				return ItemStack.EMPTY;
			}
		}

		return stack;
	}

	public static boolean canItemStacksStackRelaxed(@NotNull ItemStack a, @NotNull ItemStack b) {
		if (a.isEmpty() || b.isEmpty() || a.getItem() != b.getItem()) {
			return false;
		}

		if (!a.isStackable()) {
			return false;
		}

		if (a.hasTag() != b.hasTag()) {
			return false;
		}

		if ((!a.hasTag() || Objects.equals(a.getTag(), b.getTag()))) {
			return ContainerInventory.areCapsCompatible(a, b);
		}

		return false;
	}

	public static boolean canItemStacksStack(@NotNull ItemStack a, @NotNull ItemStack b) {
		if (a.isEmpty() || !a.sameItem(b) || a.hasTag() != b.hasTag()) {
			return false;
		}

		if ((!a.hasTag() || Objects.equals(a.getTag(), b.getTag()))) {
			return ContainerInventory.areCapsCompatible(a, b);
		}

		return false;
	}
}

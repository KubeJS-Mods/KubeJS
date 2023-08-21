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
import dev.latvian.mods.kubejs.platform.LevelPlatformHelper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ItemHandlerUtils {
	public static void giveItemToPlayer(Player player, @NotNull ItemStack stack, int preferredSlot) {
		if (stack.isEmpty()) {
			return;
		}

		InventoryKJS inventory = new PlayerMainInvWrapper(player.getInventory());
		var level = player.level();

		// try adding it into the inventory
		var remainder = stack;
		// insert into preferred slot first
		if (preferredSlot >= 0 && preferredSlot < inventory.kjs$getSlots()) {
			remainder = inventory.kjs$insertItem(preferredSlot, stack, false);
		}
		// then into the inventory in general
		if (!remainder.isEmpty()) {
			remainder = insertItemStacked(inventory, remainder, false);
		}

		// play sound if something got picked up
		if (remainder.isEmpty() || remainder.getCount() != stack.getCount()) {
			level.playSound(null, player.getX(), player.getY() + 0.5, player.getZ(),
				SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((level.random.nextFloat() - level.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
		}

		// drop remaining itemstack into the world
		if (!remainder.isEmpty() && !level.isClientSide) {
			var itemEntity = new ItemEntity(level, player.getX(), player.getY() + 0.5, player.getZ(), remainder);
			itemEntity.setPickUpDelay(40);
			itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().multiply(0, 1, 0));

			level.addFreshEntity(itemEntity);
		}
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
			if (canItemStacksStackRelaxed(slot, stack)) {
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
			return LevelPlatformHelper.get().areCapsCompatible(a, b);
		}

		return false;
	}

	public static boolean canItemStacksStack(@NotNull ItemStack a, @NotNull ItemStack b) {
		if (a.isEmpty() || !ItemStack.isSameItem(a, b) || a.hasTag() != b.hasTag()) {
			return false;
		}

		if ((!a.hasTag() || Objects.equals(a.getTag(), b.getTag()))) {
			return LevelPlatformHelper.get().areCapsCompatible(a, b);
		}

		return false;
	}
}

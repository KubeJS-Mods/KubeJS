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

package dev.latvian.kubejs.item;

import me.shedaniel.architectury.annotations.ExpectPlatform;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ContainerInventory implements ItemHandler.Mutable
{
	private final Container container;

	public ContainerInventory(Container container)
	{
		this.container = container;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		ContainerInventory that = (ContainerInventory) o;

		return getInv().equals(that.getInv());

	}

	@Override
	public int hashCode()
	{
		return getInv().hashCode();
	}

	@Override
	public int getSlots()
	{
		return getInv().getContainerSize();
	}

	@Override
	@NotNull
	public ItemStack getStackInSlot(int slot)
	{
		return getInv().getItem(slot);
	}

	@Override
	@NotNull
	public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
	{
		if (stack.isEmpty())
		{
			return ItemStack.EMPTY;
		}

		ItemStack stackInSlot = getInv().getItem(slot);

		int m;
		if (!stackInSlot.isEmpty())
		{
			if (stackInSlot.getCount() >= Math.min(stackInSlot.getMaxStackSize(), getSlotLimit(slot)))
			{
				return stack;
			}

			if (!ItemHandlerUtils.canItemStacksStack(stack, stackInSlot))
			{
				return stack;
			}

			if (!getInv().canPlaceItem(slot, stack))
			{
				return stack;
			}

			m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot)) - stackInSlot.getCount();

			if (stack.getCount() <= m)
			{
				if (!simulate)
				{
					ItemStack copy = stack.copy();
					copy.grow(stackInSlot.getCount());
					getInv().setItem(slot, copy);
					getInv().setChanged();
				}

				return ItemStack.EMPTY;
			}
			else
			{
				// copy the stack to not modify the original one
				stack = stack.copy();
				if (!simulate)
				{
					ItemStack copy = stack.split(m);
					copy.grow(stackInSlot.getCount());
					getInv().setItem(slot, copy);
					getInv().setChanged();
					return stack;
				}
				else
				{
					stack.shrink(m);
					return stack;
				}
			}
		}
		else
		{
			if (!getInv().canPlaceItem(slot, stack))
			{
				return stack;
			}

			m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));
			if (m < stack.getCount())
			{
				// copy the stack to not modify the original one
				stack = stack.copy();
				if (!simulate)
				{
					getInv().setItem(slot, stack.split(m));
					getInv().setChanged();
					return stack;
				}
				else
				{
					stack.shrink(m);
					return stack;
				}
			}
			else
			{
				if (!simulate)
				{
					getInv().setItem(slot, stack);
					getInv().setChanged();
				}
				return ItemStack.EMPTY;
			}
		}

	}

	@Override
	@NotNull
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		if (amount == 0)
		{
			return ItemStack.EMPTY;
		}

		ItemStack stackInSlot = getInv().getItem(slot);

		if (stackInSlot.isEmpty())
		{
			return ItemStack.EMPTY;
		}

		if (simulate)
		{
			if (stackInSlot.getCount() < amount)
			{
				return stackInSlot.copy();
			}
			else
			{
				ItemStack copy = stackInSlot.copy();
				copy.setCount(amount);
				return copy;
			}
		}
		else
		{
			int m = Math.min(stackInSlot.getCount(), amount);

			ItemStack decrStackSize = getInv().removeItem(slot, m);
			getInv().setChanged();
			return decrStackSize;
		}
	}

	@Override
	public void setStackInSlot(int slot, @NotNull ItemStack stack)
	{
		getInv().setItem(slot, stack);
	}

	@Override
	public int getSlotLimit(int slot)
	{
		return getInv().getMaxStackSize();
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack)
	{
		return getInv().canPlaceItem(slot, stack);
	}

	public Container getInv()
	{
		return container;
	}

	@ExpectPlatform
	public static boolean areCapsCompatible(ItemStack a, ItemStack b)
	{
		throw new AssertionError();
	}
}

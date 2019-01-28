package com.latmod.mods.kubejs.item;

import com.latmod.mods.kubejs.util.UtilsJS;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

/**
 * @author LatvianModder
 */
public class InventoryJS
{
	public static final int IGNORE_COUNT = 1;
	public static final int IGNORE_META = 2;
	public static final int IGNORE_NBT = 4;

	public final IItemHandler itemHandler;

	public InventoryJS(IItemHandler h)
	{
		itemHandler = h;
	}

	public int size()
	{
		return itemHandler.getSlots();
	}

	public ItemStackJS get(int slot)
	{
		return UtilsJS.INSTANCE.item(itemHandler.getStackInSlot(slot));
	}

	public void set(int slot, Object item)
	{
		if (itemHandler instanceof IItemHandlerModifiable)
		{
			((IItemHandlerModifiable) itemHandler).setStackInSlot(slot, UtilsJS.INSTANCE.item(item).itemStack());
		}
		else
		{
			throw new IllegalStateException("This inventory can't be modified directly! Use insert/extract methods!");
		}
	}

	public ItemStackJS insert(int slot, Object item, boolean simulate)
	{
		return UtilsJS.INSTANCE.item(itemHandler.insertItem(slot, UtilsJS.INSTANCE.item(item).itemStack(), simulate));
	}

	public ItemStackJS extract(int slot, int amount, boolean simulate)
	{
		return UtilsJS.INSTANCE.item(itemHandler.extractItem(slot, amount, simulate));
	}

	public int getSlotLimit(int slot)
	{
		return itemHandler.getSlotLimit(slot);
	}

	public boolean isItemValid(int slot, Object item)
	{
		return itemHandler.isItemValid(slot, UtilsJS.INSTANCE.item(item).itemStack());
	}

	public int find(Object item, int flags)
	{
		ItemStack stack = UtilsJS.INSTANCE.item(item).itemStack();

		for (int i = 0; i < itemHandler.getSlots(); i++)
		{
			ItemStack stack1 = itemHandler.getStackInSlot(i);

			if (stack.getItem() == stack1.getItem())
			{
				if ((flags & IGNORE_COUNT) != 0 || stack.getCount() == stack1.getCount())
				{
					if ((flags & IGNORE_META) != 0 || stack.getMetadata() == stack1.getMetadata())
					{
						if ((flags & IGNORE_NBT) != 0 || ItemStack.areItemStackShareTagsEqual(stack, stack1))
						{
							return i;
						}
					}
				}
			}
		}

		return -1;
	}

	public int count(Object item, int flags)
	{
		ItemStack stack = UtilsJS.INSTANCE.item(item).itemStack();
		int count = 0;

		for (int i = 0; i < itemHandler.getSlots(); i++)
		{
			ItemStack stack1 = itemHandler.getStackInSlot(i);

			if (stack.getItem() == stack1.getItem())
			{
				if ((flags & IGNORE_META) != 0 || stack.getMetadata() == stack1.getMetadata())
				{
					if ((flags & IGNORE_NBT) != 0 || ItemStack.areItemStackShareTagsEqual(stack, stack1))
					{
						count += stack1.getCount();
					}
				}
			}
		}

		return count;
	}
}
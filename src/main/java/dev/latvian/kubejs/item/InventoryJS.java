package dev.latvian.kubejs.item;

import dev.latvian.kubejs.util.UtilsJS;
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

	public final IItemHandler _inventory;

	public InventoryJS(IItemHandler h)
	{
		_inventory = h;
	}

	public int size()
	{
		return _inventory.getSlots();
	}

	public ItemStackJS get(int slot)
	{
		return UtilsJS.INSTANCE.item(_inventory.getStackInSlot(slot));
	}

	public void set(int slot, Object item)
	{
		if (_inventory instanceof IItemHandlerModifiable)
		{
			((IItemHandlerModifiable) _inventory).setStackInSlot(slot, UtilsJS.INSTANCE.item(item).itemStack());
		}
		else
		{
			throw new IllegalStateException("This inventory can't be modified directly! Use insert/extract methods!");
		}
	}

	public ItemStackJS insert(int slot, Object item, boolean simulate)
	{
		return UtilsJS.INSTANCE.item(_inventory.insertItem(slot, UtilsJS.INSTANCE.item(item).itemStack(), simulate));
	}

	public ItemStackJS extract(int slot, int amount, boolean simulate)
	{
		return UtilsJS.INSTANCE.item(_inventory.extractItem(slot, amount, simulate));
	}

	public int getSlotLimit(int slot)
	{
		return _inventory.getSlotLimit(slot);
	}

	public boolean isItemValid(int slot, Object item)
	{
		return _inventory.isItemValid(slot, UtilsJS.INSTANCE.item(item).itemStack());
	}

	public int find(Object item, int flags)
	{
		ItemStack stack = UtilsJS.INSTANCE.item(item).itemStack();

		for (int i = 0; i < _inventory.getSlots(); i++)
		{
			ItemStack stack1 = _inventory.getStackInSlot(i);

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

		for (int i = 0; i < _inventory.getSlots(); i++)
		{
			ItemStack stack1 = _inventory.getStackInSlot(i);

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
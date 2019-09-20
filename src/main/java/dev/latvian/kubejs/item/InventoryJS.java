package dev.latvian.kubejs.item;

import dev.latvian.kubejs.documentation.Ignore;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.documentation.T;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.item.ingredient.MatchAllIngredientJS;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

/**
 * @author LatvianModder
 */
public class InventoryJS
{
	@Ignore
	public final IItemHandler inventory;

	public InventoryJS(IItemHandler h)
	{
		inventory = h;
	}

	public int size()
	{
		return inventory.getSlots();
	}

	public ItemStackJS get(int slot)
	{
		return ItemStackJS.of(inventory.getStackInSlot(slot));
	}

	public void set(int slot, Object item)
	{
		if (inventory instanceof IItemHandlerModifiable)
		{
			((IItemHandlerModifiable) inventory).setStackInSlot(slot, ItemStackJS.of(item).getItemStack());
		}
		else
		{
			throw new IllegalStateException("This inventory can't be modified directly! Use insert/extract methods!");
		}
	}

	public ItemStackJS insert(int slot, Object item, boolean simulate)
	{
		return ItemStackJS.of(inventory.insertItem(slot, ItemStackJS.of(item).getItemStack(), simulate));
	}

	public ItemStackJS extract(int slot, int amount, boolean simulate)
	{
		return ItemStackJS.of(inventory.extractItem(slot, amount, simulate));
	}

	public int getSlotLimit(int slot)
	{
		return inventory.getSlotLimit(slot);
	}

	public boolean isItemValid(int slot, Object item)
	{
		return inventory.isItemValid(slot, ItemStackJS.of(item).getItemStack());
	}

	public void clear()
	{
		IItemHandlerModifiable modInv = inventory instanceof IItemHandlerModifiable ? (IItemHandlerModifiable) inventory : null;

		for (int i = inventory.getSlots(); i >= 0; i--)
		{
			if (modInv != null)
			{
				modInv.setStackInSlot(i, ItemStack.EMPTY);
			}
			else
			{
				inventory.extractItem(i, inventory.getStackInSlot(i).getCount(), false);
			}
		}
	}

	public void clear(@P("ingredient") @T(IngredientJS.class) Object o)
	{
		IngredientJS ingredient = IngredientJS.of(o);

		if (ingredient == MatchAllIngredientJS.INSTANCE)
		{
			clear();
		}

		IItemHandlerModifiable modInv = inventory instanceof IItemHandlerModifiable ? (IItemHandlerModifiable) inventory : null;

		for (int i = inventory.getSlots(); i >= 0; i--)
		{
			if (ingredient.test(inventory.getStackInSlot(i)))
			{
				if (modInv != null)
				{
					modInv.setStackInSlot(i, ItemStack.EMPTY);
				}
				else
				{
					inventory.extractItem(i, inventory.getStackInSlot(i).getCount(), false);
				}
			}
		}
	}

	public int find()
	{
		for (int i = 0; i < inventory.getSlots(); i++)
		{
			ItemStack stack1 = inventory.getStackInSlot(i);

			if (!stack1.isEmpty())
			{
				return i;
			}
		}

		return -1;
	}

	public int find(@P("ingredient") @T(IngredientJS.class) Object o)
	{
		IngredientJS ingredient = IngredientJS.of(o);

		if (ingredient == MatchAllIngredientJS.INSTANCE)
		{
			return find();
		}

		for (int i = 0; i < inventory.getSlots(); i++)
		{
			ItemStack stack1 = inventory.getStackInSlot(i);

			if (ingredient.test(stack1))
			{
				return i;
			}
		}

		return -1;
	}

	public int count()
	{
		int count = 0;

		for (int i = 0; i < inventory.getSlots(); i++)
		{
			count += inventory.getStackInSlot(i).getCount();
		}

		return count;
	}

	public int count(@P("ingredient") @T(IngredientJS.class) Object o)
	{
		IngredientJS ingredient = IngredientJS.of(o);

		if (ingredient == MatchAllIngredientJS.INSTANCE)
		{
			return count();
		}

		int count = 0;

		for (int i = 0; i < inventory.getSlots(); i++)
		{
			ItemStack stack1 = inventory.getStackInSlot(i);

			if (ingredient.test(stack1))
			{
				count += stack1.getCount();
			}
		}

		return count;
	}

	public boolean isEmpty()
	{
		for (int i = 0; i < inventory.getSlots(); i++)
		{
			if (!inventory.getStackInSlot(i).isEmpty())
			{
				return false;
			}
		}

		return true;
	}
}
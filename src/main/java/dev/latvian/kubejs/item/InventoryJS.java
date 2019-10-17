package dev.latvian.kubejs.item;

import dev.latvian.kubejs.MinecraftClass;
import dev.latvian.kubejs.documentation.Ignore;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.documentation.T;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.item.ingredient.MatchAllIngredientJS;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.util.LinkedList;

/**
 * @author LatvianModder
 */
public class InventoryJS
{
	@Ignore
	@MinecraftClass
	public final IItemHandler minecraftInventory;

	public InventoryJS(IItemHandler h)
	{
		minecraftInventory = h;
	}

	public int getSize()
	{
		return minecraftInventory.getSlots();
	}

	public ItemStackJS get(int slot)
	{
		return ItemStackJS.of(minecraftInventory.getStackInSlot(slot));
	}

	public void set(int slot, Object item)
	{
		if (minecraftInventory instanceof IItemHandlerModifiable)
		{
			((IItemHandlerModifiable) minecraftInventory).setStackInSlot(slot, ItemStackJS.of(item).getItemStack());
		}
		else
		{
			throw new IllegalStateException("This inventory can't be modified directly! Use insert/extract methods!");
		}
	}

	public ItemStackJS insert(int slot, Object item, boolean simulate)
	{
		return ItemStackJS.of(minecraftInventory.insertItem(slot, ItemStackJS.of(item).getItemStack(), simulate));
	}

	public ItemStackJS extract(int slot, int amount, boolean simulate)
	{
		return ItemStackJS.of(minecraftInventory.extractItem(slot, amount, simulate));
	}

	public int getSlotLimit(int slot)
	{
		return minecraftInventory.getSlotLimit(slot);
	}

	public boolean isItemValid(int slot, Object item)
	{
		return minecraftInventory.isItemValid(slot, ItemStackJS.of(item).getItemStack());
	}

	public void clear()
	{
		IItemHandlerModifiable modInv = minecraftInventory instanceof IItemHandlerModifiable ? (IItemHandlerModifiable) minecraftInventory : null;

		for (int i = minecraftInventory.getSlots(); i >= 0; i--)
		{
			if (modInv != null)
			{
				modInv.setStackInSlot(i, ItemStack.EMPTY);
			}
			else
			{
				minecraftInventory.extractItem(i, minecraftInventory.getStackInSlot(i).getCount(), false);
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

		IItemHandlerModifiable modInv = minecraftInventory instanceof IItemHandlerModifiable ? (IItemHandlerModifiable) minecraftInventory : null;

		for (int i = minecraftInventory.getSlots(); i >= 0; i--)
		{
			if (ingredient.testVanilla(minecraftInventory.getStackInSlot(i)))
			{
				if (modInv != null)
				{
					modInv.setStackInSlot(i, ItemStack.EMPTY);
				}
				else
				{
					minecraftInventory.extractItem(i, minecraftInventory.getStackInSlot(i).getCount(), false);
				}
			}
		}
	}

	public int find()
	{
		for (int i = 0; i < minecraftInventory.getSlots(); i++)
		{
			ItemStack stack1 = minecraftInventory.getStackInSlot(i);

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

		for (int i = 0; i < minecraftInventory.getSlots(); i++)
		{
			ItemStack stack1 = minecraftInventory.getStackInSlot(i);

			if (ingredient.testVanilla(stack1))
			{
				return i;
			}
		}

		return -1;
	}

	public int count()
	{
		int count = 0;

		for (int i = 0; i < minecraftInventory.getSlots(); i++)
		{
			count += minecraftInventory.getStackInSlot(i).getCount();
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

		for (int i = 0; i < minecraftInventory.getSlots(); i++)
		{
			ItemStack stack1 = minecraftInventory.getStackInSlot(i);

			if (ingredient.testVanilla(stack1))
			{
				count += stack1.getCount();
			}
		}

		return count;
	}

	public boolean isEmpty()
	{
		for (int i = 0; i < minecraftInventory.getSlots(); i++)
		{
			if (!minecraftInventory.getStackInSlot(i).isEmpty())
			{
				return false;
			}
		}

		return true;
	}

	@Override
	public String toString()
	{
		LinkedList<String> list = new LinkedList<>();

		for (int i = 0; i < getSize(); i++)
		{
			list.add(get(i).toString());
		}

		return list.toString();
	}

	public void markDirty()
	{
		if (minecraftInventory instanceof InvWrapper)
		{
			((InvWrapper) minecraftInventory).getInv().markDirty();
		}
	}
}
package dev.latvian.kubejs.item;

import dev.latvian.kubejs.documentation.DocClass;
import dev.latvian.kubejs.documentation.DocMethod;
import dev.latvian.kubejs.documentation.Param;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.item.ingredient.MatchAllIngredientJS;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

/**
 * @author LatvianModder
 */
@DocClass
public class InventoryJS
{
	public final IItemHandler inventory;

	public InventoryJS(IItemHandler h)
	{
		inventory = h;
	}

	@DocMethod
	public int size()
	{
		return inventory.getSlots();
	}

	@DocMethod
	public ItemStackJS get(int slot)
	{
		return ItemStackJS.of(inventory.getStackInSlot(slot));
	}

	@DocMethod
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

	@DocMethod
	public ItemStackJS insert(int slot, Object item, boolean simulate)
	{
		return ItemStackJS.of(inventory.insertItem(slot, ItemStackJS.of(item).getItemStack(), simulate));
	}

	@DocMethod
	public ItemStackJS extract(int slot, int amount, boolean simulate)
	{
		return ItemStackJS.of(inventory.extractItem(slot, amount, simulate));
	}

	@DocMethod
	public int getSlotLimit(int slot)
	{
		return inventory.getSlotLimit(slot);
	}

	@DocMethod
	public boolean isItemValid(int slot, Object item)
	{
		return inventory.isItemValid(slot, ItemStackJS.of(item).getItemStack());
	}

	@DocMethod
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

	@DocMethod(params = @Param(value = "ingredient", type = IngredientJS.class))
	public void clear(Object o)
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

	@DocMethod
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

	@DocMethod(params = @Param(value = "ingredient", type = IngredientJS.class))
	public int find(Object o)
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

	@DocMethod
	public int count()
	{
		int count = 0;

		for (int i = 0; i < inventory.getSlots(); i++)
		{
			count += inventory.getStackInSlot(i).getCount();
		}

		return count;
	}

	@DocMethod(params = @Param(value = "ingredient", type = IngredientJS.class))
	public int count(Object o)
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
}
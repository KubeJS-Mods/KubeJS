package dev.latvian.kubejs.item;

import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class BoundItemStackJS extends ItemStackJS
{
	private final ItemStack stack;

	public BoundItemStackJS(ItemStack is)
	{
		stack = is;
	}

	@Override
	public Item item()
	{
		return stack.getItem();
	}

	@Override
	public ItemStackJS copy()
	{
		return new BoundItemStackJS(stack.copy());
	}

	@Override
	public ItemStackJS count(int c)
	{
		stack.setCount(c);
		return this;
	}

	@Override
	public int count()
	{
		return stack.getCount();
	}

	@Override
	public boolean isEmpty()
	{
		return stack.isEmpty();
	}

	@Override
	public ItemStackJS data(int d)
	{
		stack.setItemDamage(d);
		return this;
	}

	@Override
	public int data()
	{
		return stack.getMetadata();
	}

	@Override
	public ItemStackJS nbt(@Nullable Object o)
	{
		stack.setTagCompound(UtilsJS.INSTANCE.nbt(o));
		return this;
	}

	@Override
	@Nullable
	public NBTTagCompound rawNBT()
	{
		return stack.getTagCompound();
	}

	@Override
	public ItemStackJS caps(@Nullable Object o)
	{
		NBTTagCompound n = stack.serializeNBT();
		NBTTagCompound nbt = UtilsJS.INSTANCE.nbt(o);

		if (nbt == null)
		{
			n.removeTag("ForgeCaps");
		}
		else
		{
			n.setTag("ForgeCaps", n);
		}

		stack.deserializeNBT(n);
		return this;
	}

	@Override
	@Nullable
	public NBTTagCompound rawCaps()
	{
		return (NBTTagCompound) stack.serializeNBT().getTag("ForgeCaps");
	}

	@Override
	public ItemStack itemStack()
	{
		return stack;
	}
}
package dev.latvian.kubejs.item;

import dev.latvian.kubejs.util.nbt.NBTBaseJS;
import dev.latvian.kubejs.util.nbt.NBTCompoundJS;
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
		stack.setTagCompound(NBTBaseJS.of(o).asCompound().createNBT());
		return this;
	}

	@Override
	public NBTCompoundJS nbt()
	{
		return NBTBaseJS.of(stack.getTagCompound()).asCompound();
	}

	@Override
	public ItemStackJS caps(@Nullable Object o)
	{
		NBTTagCompound n = stack.serializeNBT();
		NBTTagCompound nbt = NBTBaseJS.of(o).asCompound().createNBT();

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
	public NBTCompoundJS caps()
	{
		return NBTBaseJS.of(stack.serializeNBT().getTag("ForgeCaps")).asCompound();
	}

	@Override
	public ItemStack itemStack()
	{
		return stack;
	}
}
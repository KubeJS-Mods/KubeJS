package dev.latvian.kubejs.item;

import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class UnboundItemStackJS extends ItemStackJS
{
	private final Item item;
	private int count;
	private int data;
	private NBTTagCompound nbt;
	private NBTTagCompound caps;
	private ItemStack cached;

	public UnboundItemStackJS(Item i)
	{
		item = i;
		count = 1;
		data = 0;
		nbt = null;
		caps = null;
		cached = null;
	}

	@Override
	public Item item()
	{
		return item;
	}

	@Override
	public ItemStackJS copy()
	{
		UnboundItemStackJS stack = new UnboundItemStackJS(item);
		stack.count = count;
		stack.data = data;
		stack.nbt = nbt == null ? null : nbt.copy();
		stack.caps = caps == null ? null : caps.copy();
		return stack;
	}

	@Override
	public ItemStackJS count(int c)
	{
		count = MathHelper.clamp(c, 0, 64);

		if (cached != null)
		{
			cached.setCount(count);
		}

		return this;
	}

	@Override
	public int count()
	{
		return count;
	}

	@Override
	public ItemStackJS data(int d)
	{
		data = MathHelper.clamp(d, 0, 32767);

		if (cached != null)
		{
			cached.setItemDamage(data);
		}

		return this;
	}

	@Override
	public int data()
	{
		return data;
	}

	@Override
	public ItemStackJS nbt(@Nullable Object o)
	{
		nbt = UtilsJS.INSTANCE.nbt(o);

		if (cached != null)
		{
			cached.setTagCompound(nbt);
		}

		return this;
	}

	@Override
	@Nullable
	public NBTTagCompound rawNBT()
	{
		return nbt;
	}

	@Override
	public ItemStackJS caps(@Nullable Object o)
	{
		caps = UtilsJS.INSTANCE.nbt(o);
		cached = null;
		return this;
	}

	@Override
	@Nullable
	public NBTTagCompound rawCaps()
	{
		return caps;
	}

	@Override
	public ItemStack itemStack()
	{
		if (cached == null)
		{
			cached = new ItemStack(item, count, data, caps);
			cached.setTagCompound(nbt);
		}

		return cached;
	}
}

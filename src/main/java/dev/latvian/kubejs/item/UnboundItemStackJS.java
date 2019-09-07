package dev.latvian.kubejs.item;

import dev.latvian.kubejs.util.nbt.NBTBaseJS;
import dev.latvian.kubejs.util.nbt.NBTCompoundJS;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
	private NBTCompoundJS nbt;
	private NBTCompoundJS caps;
	private ItemStack cached;

	public UnboundItemStackJS(Item i)
	{
		item = i;
		count = 1;
		data = 0;
		nbt = NBTCompoundJS.NULL;
		caps = NBTCompoundJS.NULL;
		cached = null;
	}

	@Override
	public Item getItem()
	{
		return item;
	}

	@Override
	public ItemStackJS copy()
	{
		UnboundItemStackJS stack = new UnboundItemStackJS(item);
		stack.count = count;
		stack.data = data;
		stack.nbt = nbt.copy();
		stack.caps = caps.copy();
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
		nbt = NBTBaseJS.of(o).asCompound();

		if (cached != null)
		{
			cached.setTagCompound(nbt.createNBT());
		}

		return this;
	}

	@Override
	public NBTCompoundJS nbt()
	{
		return nbt;
	}

	@Override
	public ItemStackJS caps(@Nullable Object o)
	{
		caps = NBTBaseJS.of(o).asCompound();
		cached = null;
		return this;
	}

	@Override
	public NBTCompoundJS caps()
	{
		return caps;
	}

	@Override
	public ItemStack itemStack()
	{
		if (cached == null)
		{
			cached = new ItemStack(item, count, data, caps.createNBT());
			cached.setTagCompound(nbt.createNBT());
		}

		return cached;
	}
}

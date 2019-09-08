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
	private ItemStack cached;

	public UnboundItemStackJS(Item i)
	{
		item = i;
		count = 1;
		data = 0;
		nbt = NBTCompoundJS.NULL;
		cached = null;
	}

	@Override
	public Item getItem()
	{
		return item;
	}

	@Override
	public ItemStack getItemStack()
	{
		if (cached == null)
		{
			cached = new ItemStack(item, count, data);
			cached.setTagCompound(nbt.createNBT());
		}

		return cached;
	}

	@Override
	public ItemStackJS getCopy()
	{
		UnboundItemStackJS stack = new UnboundItemStackJS(item);
		stack.count = count;
		stack.data = data;
		stack.nbt = nbt.copy();
		return stack;
	}

	@Override
	public void setCount(int c)
	{
		count = MathHelper.clamp(c, 0, 64);
		cached = null;
	}

	@Override
	public int getCount()
	{
		return count;
	}

	@Override
	public void setData(int d)
	{
		data = MathHelper.clamp(d, 0, 32767);
		cached = null;
	}

	@Override
	public int getData()
	{
		return data;
	}

	@Override
	public void setNbt(@Nullable Object n)
	{
		nbt = NBTBaseJS.of(n).asCompound();
		cached = null;
	}

	@Override
	public NBTCompoundJS getNbt()
	{
		return nbt;
	}
}

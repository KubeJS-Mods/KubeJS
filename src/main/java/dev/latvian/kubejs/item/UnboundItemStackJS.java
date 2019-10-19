package dev.latvian.kubejs.item;

import dev.latvian.kubejs.util.ID;
import dev.latvian.kubejs.util.nbt.NBTBaseJS;
import dev.latvian.kubejs.util.nbt.NBTCompoundJS;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class UnboundItemStackJS extends ItemStackJS
{
	private final ResourceLocation item;
	private int count;
	private int data;
	private NBTCompoundJS nbt;
	private ItemStack cached;

	public UnboundItemStackJS(ResourceLocation i)
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
		Item i = Item.REGISTRY.getObject(item);

		if (i != null)
		{
			return i;
		}

		return Items.AIR;
	}

	@Override
	public ItemStack getItemStack()
	{
		if (cached == null)
		{
			Item i = getItem();

			if (i == Items.AIR)
			{
				return ItemStack.EMPTY;
			}

			cached = new ItemStack(i, count, data);
			cached.setTagCompound(nbt.createNBT());
		}

		return cached;
	}

	@Override
	public ID getId()
	{
		return ID.of(item);
	}

	@Override
	public boolean isEmpty()
	{
		return super.isEmpty() || getItem() == Items.AIR;
	}

	@Override
	public ItemStackJS getCopy()
	{
		UnboundItemStackJS stack = new UnboundItemStackJS(item);
		stack.count = count;
		stack.data = data;
		stack.nbt = nbt.getCopy();
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

	@Override
	public boolean areItemsEqual(ItemStackJS stack)
	{
		if (stack instanceof UnboundItemStackJS)
		{
			return item.equals(((UnboundItemStackJS) stack).item);
		}

		return getItem() == stack.getItem();
	}

	@Override
	public boolean areItemsEqual(ItemStack stack)
	{
		return item.equals(stack.getItem().getRegistryName());
	}
}

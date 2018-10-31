package com.latmod.mods.worldjs.item;

import com.latmod.mods.worldjs.util.ID;
import com.latmod.mods.worldjs.util.UtilsJS;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public abstract class ItemStackJS
{
	public static final ItemStackJS EMPTY = new ItemStackJS(ItemStack.EMPTY)
	{
		@Override
		public ItemStackJS copy()
		{
			return this;
		}

		@Override
		public ItemStackJS count(int c)
		{
			return this;
		}

		@Override
		public int count()
		{
			return 0;
		}

		@Override
		public boolean isEmpty()
		{
			return true;
		}

		@Override
		public ItemStackJS data(int d)
		{
			return this;
		}

		@Override
		public int data()
		{
			return 0;
		}

		@Override
		public ItemStackJS nbt(@Nullable Object o)
		{
			return this;
		}

		@Override
		@Nullable
		public NBTTagCompound rawNBT()
		{
			return null;
		}

		@Override
		public ItemStackJS caps(@Nullable Object o)
		{
			return this;
		}

		@Override
		@Nullable
		public NBTTagCompound rawCaps()
		{
			return null;
		}

		@Override
		public ItemStack itemStack()
		{
			return ItemStack.EMPTY;
		}

		public String toString()
		{
			return "air";
		}
	};

	public static class Bound extends ItemStackJS
	{
		private final ItemStack stack;

		public Bound(ItemStack is)
		{
			super(is.getItem());
			stack = is;
		}

		@Override
		public ItemStackJS copy()
		{
			return new Bound(stack.copy());
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
			stack.setTagCompound(UtilsJS.INSTANCE.toNBT(o));
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
			NBTTagCompound nbt = UtilsJS.INSTANCE.toNBT(o);

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

	public static class Unbound extends ItemStackJS
	{
		private int count;
		private int data;
		private NBTTagCompound nbt;
		private NBTTagCompound caps;
		private ItemStack cached;

		public Unbound(Item i)
		{
			super(i);
			count = 1;
			data = 0;
			nbt = null;
			caps = null;
			cached = null;
		}

		@Override
		public ItemStackJS copy()
		{
			Unbound stack = new Unbound(item);
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
			nbt = UtilsJS.INSTANCE.toNBT(o);

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
			caps = UtilsJS.INSTANCE.toNBT(o);
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

	public final Item item;

	public ItemStackJS(Item i)
	{
		item = i;
	}

	public ItemStackJS(ItemStack stack)
	{
		item = stack.getItem();
	}

	public ID id()
	{
		return new ID(item.getRegistryName());
	}

	public abstract ItemStackJS copy();

	public abstract ItemStackJS count(int c);

	public abstract int count();

	public boolean isEmpty()
	{
		return count() <= 0;
	}

	public abstract ItemStackJS data(int d);

	public abstract int data();

	public abstract ItemStackJS nbt(@Nullable Object o);

	@Nullable
	public abstract NBTTagCompound rawNBT();

	public abstract ItemStackJS caps(@Nullable Object o);

	@Nullable
	public abstract NBTTagCompound rawCaps();

	public abstract ItemStack itemStack();

	public ItemStackJS grow(int c)
	{
		return count(count() + c);
	}

	public ItemStackJS shrink(int c)
	{
		return grow(-c);
	}

	public String nbt()
	{
		NBTTagCompound nbt = rawNBT();
		return nbt == null ? "null" : nbt.toString();
	}

	public String caps()
	{
		NBTTagCompound nbt = rawCaps();
		return nbt == null ? "null" : nbt.toString();
	}

	public ItemStackJS name(String displayName)
	{
		NBTTagCompound nbt = rawNBT();

		if (nbt == null)
		{
			nbt = new NBTTagCompound();
		}

		NBTTagCompound n = nbt.getCompoundTag("display");
		n.setString("Name", displayName);
		nbt.setTag("display", n);
		return nbt(nbt);
	}

	public String name()
	{
		return itemStack().getDisplayName();
	}

	public String toString()
	{
		StringBuilder builder = new StringBuilder();

		if (count() > 1)
		{
			builder.append(count());
			builder.append('x');
			builder.append(' ');
		}

		builder.append(item.getRegistryName());

		if (item.getHasSubtypes())
		{
			builder.append('@');
			builder.append(data());
		}

		NBTTagCompound nbt = rawNBT();
		NBTTagCompound caps = rawCaps();

		if (nbt != null || caps != null)
		{
			builder.append(' ');
			builder.append(nbt());
		}

		if (caps != null)
		{
			builder.append(' ');
			builder.append(caps());
		}

		return builder.toString();
	}
}
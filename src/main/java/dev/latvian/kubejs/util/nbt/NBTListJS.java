package dev.latvian.kubejs.util.nbt;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLongArray;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author LatvianModder
 */
public class NBTListJS implements NBTBaseJS, Iterable<NBTBaseJS>
{
	public static final NBTListJS NULL = new NBTListJS(0)
	{
		@Override
		public int hashCode()
		{
			return 0;
		}

		@Override
		public String toString()
		{
			return "null";
		}

		@Override
		public boolean equals(Object o)
		{
			return o == this;
		}

		@Override
		public boolean isNull()
		{
			return true;
		}

		@Nullable
		@Override
		public NBTTagList createNBT()
		{
			return null;
		}

		@Override
		public int getSize()
		{
			return 0;
		}

		@Override
		public boolean isEmpty()
		{
			return true;
		}

		@Override
		public NBTListJS copy()
		{
			return this;
		}

		@Override
		public void add(Object o)
		{
		}

		@Override
		public NBTBaseJS remove(int index)
		{
			return NBTNullJS.INSTANCE;
		}

		@Override
		public NBTBaseJS get(int index)
		{
			return NBTNullJS.INSTANCE;
		}

		@Override
		public NBTBaseJS set(int index, Object value)
		{
			return NBTNullJS.INSTANCE;
		}

		@Override
		public NBTCompoundJS compoundOrNew(int index)
		{
			return NBTCompoundJS.NULL;
		}

		@Override
		public NBTListJS listOrNew(int index)
		{
			return this;
		}
	};

	private final List<NBTBaseJS> list;

	public NBTListJS(int size)
	{
		list = new ArrayList<>(size);
	}

	public NBTListJS()
	{
		this(3);
	}

	public NBTListJS(NBTTagList p)
	{
		this(p.tagCount());

		for (NBTBase nbt : p)
		{
			add(NBTBaseJS.of(nbt));
		}
	}

	@Override
	public NBTListJS asList()
	{
		return this;
	}

	@Override
	@Nullable
	public NBTBase createNBT()
	{
		NBTTagList tagList = new NBTTagList();

		if (isEmpty())
		{
			return tagList;
		}

		switch (get(0).getId())
		{
			case Constants.NBT.TAG_BYTE:
				return new NBTTagByteArray(asByteArray());
			case Constants.NBT.TAG_INT:
				return new NBTTagIntArray(asIntArray());
			case Constants.NBT.TAG_LONG:
				return new NBTTagLongArray(asLongArray());
		}

		for (NBTBaseJS nbt : this)
		{
			NBTBase base = nbt.createNBT();

			if (base != null)
			{
				tagList.appendTag(base);
			}
		}

		return tagList;
	}

	@Override
	public boolean isEmpty()
	{
		return list.isEmpty();
	}

	public int getSize()
	{
		return list.size();
	}

	public void add(Object o)
	{
		NBTBaseJS nbt = NBTBaseJS.of(o);

		if (!nbt.isNull())
		{
			list.add(nbt);
		}
	}

	public NBTBaseJS remove(int index)
	{
		if (index >= 0 && index < list.size())
		{
			NBTBaseJS nbt = NBTBaseJS.of(list.get(index));
			list.remove(index);
			return nbt;
		}

		return NBTNullJS.INSTANCE;
	}

	public NBTBaseJS get(int index)
	{
		return index < 0 || index >= list.size() ? NBTNullJS.INSTANCE : list.get(index);
	}

	public NBTBaseJS set(int index, Object value)
	{
		if (index < 0 || index >= list.size())
		{
			return NBTNullJS.INSTANCE;
		}
		else
		{
			NBTBaseJS nbt = list.set(index, NBTBaseJS.of(value));
			return nbt == null ? NBTNullJS.INSTANCE : nbt;
		}
	}

	public NBTCompoundJS compoundOrNew(int index)
	{
		NBTCompoundJS nbt = get(index).asCompound();

		if (nbt.isNull())
		{
			nbt = new NBTCompoundJS();
			set(index, nbt);
		}

		return nbt;
	}

	public NBTListJS listOrNew(int index)
	{
		NBTListJS nbt = get(index).asList();

		if (nbt.isNull())
		{
			nbt = new NBTListJS();
			set(index, nbt);
		}

		return nbt;
	}

	@Override
	public byte[] asByteArray()
	{
		byte[] a = new byte[getSize()];

		for (int i = 0; i < a.length; i++)
		{
			a[i] = list.get(i).asByte();
		}

		return a;
	}

	@Override
	public int[] asIntArray()
	{
		int[] a = new int[getSize()];

		for (int i = 0; i < a.length; i++)
		{
			a[i] = list.get(i).asInt();
		}

		return a;
	}

	@Override
	public long[] asLongArray()
	{
		long[] a = new long[getSize()];

		for (int i = 0; i < a.length; i++)
		{
			a[i] = list.get(i).asLong();
		}

		return a;
	}

	@Override
	public Iterator<NBTBaseJS> iterator()
	{
		return list.iterator();
	}
}
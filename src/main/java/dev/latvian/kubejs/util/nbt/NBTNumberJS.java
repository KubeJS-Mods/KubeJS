package dev.latvian.kubejs.util.nbt;

import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagShort;
import net.minecraftforge.common.util.Constants;

/**
 * @author LatvianModder
 */
public class NBTNumberJS implements NBTBaseJS
{
	private final Number number;
	private NBTPrimitive cached;

	public NBTNumberJS(Number n)
	{
		number = n;
		cached = null;
	}

	public Number getNumber()
	{
		return number;
	}

	public NBTNumberJS(NBTPrimitive p)
	{
		cached = p;

		switch (cached.getId())
		{
			case Constants.NBT.TAG_BYTE:
				number = p.getByte();
				break;
			case Constants.NBT.TAG_SHORT:
				number = p.getShort();
				break;
			case Constants.NBT.TAG_INT:
				number = p.getInt();
				break;
			case Constants.NBT.TAG_LONG:
				number = p.getLong();
				break;
			case Constants.NBT.TAG_FLOAT:
				number = p.getFloat();
				break;
			case Constants.NBT.TAG_DOUBLE:
			case Constants.NBT.TAG_ANY_NUMERIC:
				number = p.getDouble();
				break;
			default:
				number = 0;
		}
	}

	@Override
	public NBTPrimitive createNBT()
	{
		if (cached == null)
		{
			if (number instanceof Byte)
			{
				cached = new NBTTagByte(number.byteValue());
			}
			else if (number instanceof Short)
			{
				cached = new NBTTagShort(number.shortValue());
			}
			else if (number instanceof Integer)
			{
				cached = new NBTTagInt(number.intValue());
			}
			else if (number instanceof Float)
			{
				cached = new NBTTagFloat(number.floatValue());
			}
			else
			{
				cached = new NBTTagDouble(number.doubleValue());
			}
		}

		return cached;
	}

	@Override
	public String asString()
	{
		return asNumber().toString();
	}

	@Override
	public Number asNumber()
	{
		return number;
	}

	@Override
	public byte[] asByteArray()
	{
		return new byte[] {asByte()};
	}

	@Override
	public int[] asIntArray()
	{
		return new int[] {asInt()};
	}

	@Override
	public long[] asLongArray()
	{
		return new long[] {asLong()};
	}

	@Override
	public byte getID()
	{
		return createNBT().getId();
	}

	@Override
	public int hashCode()
	{
		return number.hashCode();
	}

	public boolean equals(Object o)
	{
		return o == this || o instanceof NBTNumberJS && number.equals(((NBTNumberJS) o).number);
	}

	@Override
	public String toString()
	{
		return createNBT().toString();
	}
}
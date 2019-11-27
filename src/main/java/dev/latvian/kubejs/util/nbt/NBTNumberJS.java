package dev.latvian.kubejs.util.nbt;

import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.NumberNBT;
import net.minecraft.nbt.ShortNBT;
import net.minecraftforge.common.util.Constants;

/**
 * @author LatvianModder
 */
public class NBTNumberJS implements NBTBaseJS
{
	private final Number number;
	private NumberNBT cached;

	public NBTNumberJS(Number n)
	{
		number = n;
		cached = null;
	}

	public Number getNumber()
	{
		return number;
	}

	public NBTNumberJS(NumberNBT p)
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
	public NumberNBT createNBT()
	{
		if (cached == null)
		{
			if (number instanceof Byte)
			{
				cached = new ByteNBT(number.byteValue());
			}
			else if (number instanceof Short)
			{
				cached = new ShortNBT(number.shortValue());
			}
			else if (number instanceof Integer)
			{
				cached = new IntNBT(number.intValue());
			}
			else if (number instanceof Float)
			{
				cached = new FloatNBT(number.floatValue());
			}
			else
			{
				cached = new DoubleNBT(number.doubleValue());
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
	public byte getId()
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
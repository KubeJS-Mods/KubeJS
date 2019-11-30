package dev.latvian.kubejs.util.nbt;

import dev.latvian.kubejs.MinecraftClass;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.EndNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NumberNBT;
import net.minecraft.nbt.StringNBT;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author LatvianModder
 */
public abstract class NBTBaseJS
{
	public static NBTBaseJS of(@Nullable Object o)
	{
		if (o == null || o instanceof EndNBT)
		{
			return NBTNullJS.INSTANCE;
		}

		return ofNormalized(UtilsJS.normalize(o));
	}

	private static NBTBaseJS ofNormalized(@Nullable Object o)
	{
		if (o == null)
		{
			return NBTNullJS.INSTANCE;
		}
		else if (o instanceof NBTBaseJS)
		{
			return (NBTBaseJS) o;
		}
		else if (o instanceof CharSequence)
		{
			return new NBTStringJS(o.toString());
		}
		else if (o instanceof Number)
		{
			return new NBTNumberJS((Number) o);
		}
		else if (o instanceof NumberNBT)
		{
			return new NBTNumberJS((NumberNBT) o);
		}
		else if (o instanceof CompoundNBT)
		{
			NBTCompoundJS map = new NBTCompoundJS();

			for (String s : ((CompoundNBT) o).keySet())
			{
				map.set(s, ofNormalized(((CompoundNBT) o).get(s)));
			}

			return new NBTCompoundJS((CompoundNBT) o);
		}
		else if (o instanceof ListNBT)
		{
			NBTListJS list = new NBTListJS();

			for (INBT nbt : (ListNBT) o)
			{
				list.add(ofNormalized(nbt));
			}

			return list;
		}
		else if (o instanceof StringNBT)
		{
			return new NBTStringJS(((StringNBT) o).getString());
		}
		else if (o instanceof Map)
		{
			NBTCompoundJS compound = new NBTCompoundJS();

			for (Map.Entry entry : ((Map<?, ?>) o).entrySet())
			{
				compound.set(entry.getKey().toString(), ofNormalized(entry.getValue()));
			}

			return compound;
		}
		else if (o instanceof Iterable)
		{
			NBTListJS list = new NBTListJS();

			for (Object o1 : (Iterable) o)
			{
				list.add(ofNormalized(o1));
			}

			return list;
		}

		return NBTNullJS.INSTANCE;
	}

	@Nullable
	@MinecraftClass
	public abstract INBT createNBT();

	public String getNbtString()
	{
		INBT nbt = createNBT();
		return nbt == null ? "null" : nbt.toString();
	}

	public boolean isEmpty()
	{
		return false;
	}

	public boolean isNull()
	{
		return false;
	}

	public byte getId()
	{
		INBT nbt = createNBT();
		return nbt == null ? 0 : nbt.getId();
	}

	public NBTBaseJS getCopy()
	{
		return this;
	}

	public NBTCompoundJS asCompound()
	{
		return new NBTCompoundJS(new CompoundNBT());
	}

	public NBTListJS asList()
	{
		NBTListJS list = new NBTListJS(new ListNBT());
		list.add(this);
		return list;
	}

	public String asString()
	{
		INBT nbt = createNBT();
		return nbt == null ? "" : nbt.toString();
	}

	public Number asNumber()
	{
		return 0;
	}

	public byte asByte()
	{
		return asNumber().byteValue();
	}

	public int asInt()
	{
		return asNumber().intValue();
	}

	public short asShort()
	{
		return asNumber().shortValue();
	}

	public long asLong()
	{
		return asNumber().longValue();
	}

	public float asFloat()
	{
		return asNumber().floatValue();
	}

	public double asDouble()
	{
		return asNumber().doubleValue();
	}

	public byte[] asByteArray()
	{
		return new byte[0];
	}

	public int[] asIntArray()
	{
		return new int[0];
	}

	public long[] asLongArray()
	{
		return new long[0];
	}
}
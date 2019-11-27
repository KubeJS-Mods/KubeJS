package dev.latvian.kubejs.util.nbt;

import dev.latvian.kubejs.MinecraftClass;
import jdk.nashorn.api.scripting.JSObject;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.EndNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NumberNBT;
import net.minecraft.nbt.StringNBT;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public interface NBTBaseJS
{
	static NBTBaseJS of(@Nullable Object o)
	{
		if (o == null || o instanceof EndNBT)
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
			return new NBTCompoundJS((CompoundNBT) o);
		}
		else if (o instanceof ListNBT)
		{
			return new NBTListJS((ListNBT) o);
		}
		else if (o instanceof StringNBT)
		{
			return new NBTStringJS(((StringNBT) o).getString());
		}
		else if (o instanceof JSObject)
		{
			JSObject js = (JSObject) o;

			if (js.isArray())
			{
				NBTListJS list = new NBTListJS();

				for (Object o1 : js.values())
				{
					list.add(of(o1));
				}

				return list;
			}

			NBTCompoundJS compound = new NBTCompoundJS();

			for (String key : js.keySet())
			{
				compound.set(key, of(js.getMember(key)));
			}

			return compound;
		}

		return NBTNullJS.INSTANCE;
	}

	@Nullable
	@MinecraftClass
	INBT createNBT();

	default String getNbtString()
	{
		INBT nbt = createNBT();
		return nbt == null ? "null" : nbt.toString();
	}

	default boolean isEmpty()
	{
		return false;
	}

	default boolean isNull()
	{
		return false;
	}

	default byte getId()
	{
		INBT nbt = createNBT();
		return nbt == null ? 0 : nbt.getId();
	}

	default NBTBaseJS getCopy()
	{
		return this;
	}

	default NBTCompoundJS asCompound()
	{
		return new NBTCompoundJS(new CompoundNBT());
	}

	default NBTListJS asList()
	{
		NBTListJS list = new NBTListJS(new ListNBT());
		list.add(this);
		return list;
	}

	default String asString()
	{
		INBT nbt = createNBT();
		return nbt == null ? "" : nbt.toString();
	}

	default Number asNumber()
	{
		return 0;
	}

	default byte asByte()
	{
		return asNumber().byteValue();
	}

	default int asInt()
	{
		return asNumber().intValue();
	}

	default short asShort()
	{
		return asNumber().shortValue();
	}

	default long asLong()
	{
		return asNumber().longValue();
	}

	default float asFloat()
	{
		return asNumber().floatValue();
	}

	default double asDouble()
	{
		return asNumber().doubleValue();
	}

	default byte[] asByteArray()
	{
		return new byte[0];
	}

	default int[] asIntArray()
	{
		return new int[0];
	}

	default long[] asLongArray()
	{
		return new long[0];
	}
}
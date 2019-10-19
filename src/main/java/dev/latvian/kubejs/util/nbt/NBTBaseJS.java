package dev.latvian.kubejs.util.nbt;

import dev.latvian.kubejs.MinecraftClass;
import jdk.nashorn.api.scripting.JSObject;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagEnd;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public interface NBTBaseJS
{
	static NBTBaseJS of(@Nullable Object o)
	{
		if (o == null || o instanceof NBTTagEnd)
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
		else if (o instanceof NBTPrimitive)
		{
			return new NBTNumberJS((NBTPrimitive) o);
		}
		else if (o instanceof NBTTagCompound)
		{
			return new NBTCompoundJS((NBTTagCompound) o);
		}
		else if (o instanceof NBTTagList)
		{
			return new NBTListJS((NBTTagList) o);
		}
		else if (o instanceof NBTTagString)
		{
			return new NBTStringJS(((NBTTagString) o).getString());
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
	NBTBase createNBT();

	default String getNbtString()
	{
		NBTBase nbt = createNBT();
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
		NBTBase nbt = createNBT();
		return nbt == null ? 0 : nbt.getId();
	}

	default NBTBaseJS getCopy()
	{
		return this;
	}

	default NBTCompoundJS asCompound()
	{
		return new NBTCompoundJS(new NBTTagCompound());
	}

	default NBTListJS asList()
	{
		NBTListJS list = new NBTListJS(new NBTTagList());
		list.add(this);
		return list;
	}

	default String asString()
	{
		NBTBase nbt = createNBT();
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
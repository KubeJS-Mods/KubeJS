package dev.latvian.kubejs.util.nbt;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class NBTStringJS implements NBTBaseJS
{
	public static final NBTStringJS EMPTY_STRING = new NBTStringJS("");

	public final String string;

	public NBTStringJS(String s)
	{
		string = s;
	}

	public boolean equals(Object o)
	{
		return o == this || o instanceof NBTStringJS && string.equals(((NBTStringJS) o).string);
	}

	public String toString()
	{
		return string;
	}

	public int hashCode()
	{
		return 0;
	}

	@Nullable
	@Override
	public NBTBase createNBT()
	{
		return new NBTTagString(string);
	}
}
package com.latmod.mods.kubejs.util;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Locale;

/**
 * @author LatvianModder
 */
public final class ID implements Comparable<ID>
{
	public final String namespace;
	public final String path;

	public ID(String resourceName)
	{
		int i = resourceName.indexOf(':');

		if (i == -1)
		{
			namespace = "minecraft";
			path = resourceName.toLowerCase(Locale.ROOT);
		}
		else
		{
			namespace = resourceName.substring(0, i).toLowerCase(Locale.ROOT);
			path = resourceName.substring(i + 1).toLowerCase(Locale.ROOT);
		}
	}

	public ID(String namespace, String path)
	{
		this(namespace.isEmpty() ? path : (namespace + ':' + path));
	}

	public ID(@Nullable ResourceLocation id)
	{
		this(id == null ? "minecraft:null" : id.toString());
	}

	public static String[] splitObjectName(String toSplit)
	{
		String[] astring = new String[] {"minecraft", toSplit};
		int i = toSplit.indexOf(58);

		if (i >= 0)
		{
			astring[1] = toSplit.substring(i + 1);

			if (i > 1)
			{
				astring[0] = toSplit.substring(0, i);
			}
		}

		return astring;
	}

	public String toString()
	{
		return namespace + ':' + path;
	}

	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		else if (o instanceof ID)
		{
			ID id = (ID) o;
			return namespace.equals(id.namespace) && path.equals(id.path);
		}

		return false;
	}

	public int hashCode()
	{
		return 31 * namespace.hashCode() + path.hashCode();
	}

	@Override
	public int compareTo(ID id)
	{
		int i = namespace.compareTo(id.namespace);
		return i == 0 ? path.compareTo(id.path) : i;
	}
}
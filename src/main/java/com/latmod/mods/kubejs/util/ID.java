package com.latmod.mods.kubejs.util;

import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Validate;

import java.util.Locale;

/**
 * @author LatvianModder
 */
public final class ID implements Comparable<ID>
{
	public final String namespace;
	public final String path;

	private ID(int unused, String... resourceName)
	{
		namespace = org.apache.commons.lang3.StringUtils.isEmpty(resourceName[0]) ? "minecraft" : resourceName[0].toLowerCase(Locale.ROOT);
		path = resourceName[1].toLowerCase(Locale.ROOT);
		Validate.notNull(path);
	}

	public ID(String resourceName)
	{
		this(0, splitObjectName(resourceName));
	}

	public ID(String namespace, String path)
	{
		this(0, namespace, path);
	}

	public ID(ResourceLocation id)
	{
		this(0, id.getNamespace(), id.getPath());
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

	public ResourceLocation getResourceLocation()
	{
		return new ResourceLocation(namespace, path);
	}
}
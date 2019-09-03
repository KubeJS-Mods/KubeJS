package dev.latvian.kubejs.util;

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

	public ID(@Nullable Object id)
	{
		if (id == null)
		{
			namespace = "minecraft";
			path = "null";
		}
		else if (id instanceof ResourceLocation)
		{
			namespace = ((ResourceLocation) id).getNamespace();
			path = ((ResourceLocation) id).getPath();
		}
		else if (id instanceof ID)
		{
			namespace = ((ID) id).namespace;
			path = ((ID) id).path;
		}
		else
		{
			String resourceName = id.toString();

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
	}

	public ID(String namespace, String path)
	{
		this(namespace.isEmpty() ? path : (namespace + ':' + path));
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

	public ResourceLocation mc()
	{
		return new ResourceLocation(namespace, path);
	}
}
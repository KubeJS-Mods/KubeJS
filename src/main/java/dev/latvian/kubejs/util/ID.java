package dev.latvian.kubejs.util;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Locale;

/**
 * @author LatvianModder
 */
public final class ID implements Comparable<ID>
{
	private static final ID NULL_ID = new ID("minecraft", "null");

	public final String namespace;
	public final String path;

	public static ID of(@Nullable Object id)
	{
		if (id == null)
		{
			return NULL_ID;
		}
		else if (id instanceof ID)
		{
			return (ID) id;
		}
		else if (id instanceof ResourceLocation)
		{
			ResourceLocation r = (ResourceLocation) id;
			return new ID(r.getNamespace(), r.getPath());
		}

		String s = id.toString();

		int i = s.indexOf(':');

		if (i == -1)
		{
			return new ID("minecraft", s.toLowerCase(Locale.ROOT));
		}

		return new ID(s.substring(0, i).toLowerCase(Locale.ROOT), s.substring(i + 1).toLowerCase(Locale.ROOT));
	}

	public static ID of(String namespace, String path)
	{
		return namespace.isEmpty() ? of(path) : of(namespace + ':' + path);
	}

	private ID(String n, String p)
	{
		namespace = n;
		path = p;
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
		else if (o instanceof CharSequence)
		{
			return o.toString().equals(toString());
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
package dev.latvian.kubejs.util;

import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.world.ClientWorldJS;
import dev.latvian.kubejs.world.WorldJS;
import jdk.nashorn.api.scripting.JSObject;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author LatvianModder
 */
public class UtilsJS
{
	public static final Random RANDOM = new Random();

	private static Map<ID, StatBase> statMap;

	public static void init()
	{
		statMap = new HashMap<>(StatList.ALL_STATS.size());

		for (StatBase stat : StatList.ALL_STATS)
		{
			statMap.put(ID.of(stat.statId), stat);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T cast(Object o)
	{
		return (T) o;
	}

	@SuppressWarnings("unchecked")
	public static Collection<Object> getList(Object o)
	{
		if (o instanceof Collection)
		{
			return (Collection) o;
		}
		else if (o instanceof Iterable)
		{
			List<Object> list = new ArrayList<>();

			for (Object o1 : (Iterable) o)
			{
				list.add(o1);
			}

			return list;
		}
		else if (o instanceof JSObject && ((JSObject) o).isArray())
		{
			return ((JSObject) o).values();
		}
		else if (o instanceof Object[])
		{
			return Arrays.asList(((Object[]) o));
		}

		return Collections.singleton(o);
	}

	public static FieldJS getField(String className, String fieldName)
	{
		try
		{
			return getField(Class.forName(className), fieldName);
		}
		catch (Throwable ex)
		{
			return new FieldJS(null);
		}
	}

	public static FieldJS getField(String className, String fieldName, String obfFieldName)
	{
		try
		{
			return getField(Class.forName(className), fieldName, obfFieldName);
		}
		catch (Throwable ex)
		{
			return new FieldJS(null);
		}
	}

	@SuppressWarnings("deprecation")
	public static FieldJS getField(Class className, String fieldName)
	{
		try
		{
			return new FieldJS(net.minecraftforge.fml.relauncher.ReflectionHelper.findField(className, fieldName));
		}
		catch (Throwable ex)
		{
			return new FieldJS(null);
		}
	}

	@SuppressWarnings("deprecation")
	public static FieldJS getField(Class className, String fieldName, String obfFieldName)
	{
		try
		{
			return new FieldJS(net.minecraftforge.fml.relauncher.ReflectionHelper.findField(className, fieldName, obfFieldName));
		}
		catch (Throwable ex)
		{
			return new FieldJS(null);
		}
	}

	public static int parseInt(@Nullable Object object, int def)
	{
		if (object == null)
		{
			return def;
		}
		else if (object instanceof Number)
		{
			return ((Number) object).intValue();
		}

		try
		{
			String s = object.toString();

			if (s.isEmpty())
			{
				return def;
			}

			return Integer.parseInt(s);
		}
		catch (Exception ex)
		{
			return def;
		}
	}

	public static double parseDouble(@Nullable Object object, double def)
	{
		if (object == null)
		{
			return def;
		}
		else if (object instanceof Number)
		{
			return ((Number) object).doubleValue();
		}

		try
		{
			String s = object.toString();

			if (s.isEmpty())
			{
				return def;
			}

			return Double.parseDouble(String.valueOf(object));
		}
		catch (Exception ex)
		{
			return def;
		}
	}

	@Nullable
	public static StatBase getStat(@Nullable Object id)
	{
		if (id == null)
		{
			return null;
		}
		else if (id instanceof StatBase)
		{
			return (StatBase) id;
		}

		return statMap.get(ID.of(id));
	}

	public static String getToolType(String id)
	{
		return id;
	}

	public static WorldJS getWorld(World world)
	{
		if (world.isRemote)
		{
			return getClientWorld();
		}
		else
		{
			return ServerJS.instance.getWorld(world);
		}
	}

	public static WorldJS getClientWorld()
	{
		return ClientWorldJS.get();
	}
}
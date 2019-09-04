package dev.latvian.kubejs.util;

import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.world.ClientWorldJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;
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
			statMap.put(new ID(stat.statId), stat);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T cast(Object o)
	{
		return (T) o;
	}

	@SuppressWarnings("deprecation")
	public static FieldJS field(String className, String fieldName)
	{
		try
		{
			return new FieldJS(net.minecraftforge.fml.relauncher.ReflectionHelper.findField(Class.forName(className), fieldName));
		}
		catch (Throwable ex)
		{
			return new FieldJS(null);
		}
	}

	@SuppressWarnings("deprecation")
	public static FieldJS field(Class className, String fieldName)
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

	public static int parseInt(@Nullable Object object, int def)
	{
		if (object instanceof Number)
		{
			return ((Number) object).intValue();
		}

		try
		{
			return Integer.parseInt(String.valueOf(object));
		}
		catch (Exception ex)
		{
			return def;
		}
	}

	public static double parseDouble(@Nullable Object object, double def)
	{
		if (object instanceof Number)
		{
			return ((Number) object).doubleValue();
		}

		try
		{
			return Double.parseDouble(String.valueOf(object));
		}
		catch (Exception ex)
		{
			return def;
		}
	}

	@Nullable
	public static StatBase stat(@Nullable Object id)
	{
		if (id == null)
		{
			return null;
		}
		else if (id instanceof StatBase)
		{
			return (StatBase) id;
		}

		return statMap.get(new ID(id));
	}

	public static String toolType(String id)
	{
		return id;
	}

	public static WorldJS world(World world)
	{
		if (world.isRemote)
		{
			return clientWorld();
		}
		else
		{
			return ServerJS.instance.world(world);
		}
	}

	public static WorldJS clientWorld()
	{
		return ClientWorldJS.get();
	}
}
package dev.latvian.kubejs.util;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.world.ClientWorldJS;
import dev.latvian.kubejs.world.WorldJS;
import jdk.nashorn.api.scripting.JSObject;
import net.minecraft.potion.Effect;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorld;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author LatvianModder
 */
public class UtilsJS
{
	public static final ResourceLocation NULL_ID = new ResourceLocation("minecraft", "null");
	public static final Random RANDOM = new Random();

	public static void init()
	{
	}

	@SuppressWarnings("unchecked")
	public static <T> T cast(Object o)
	{
		return (T) o;
	}

	public static void queueIO(Runnable runnable)
	{
		/*FIXME: ThreadedFileIOBase.getThreadedIOInstance().queueIO(() -> {

			try
			{
				runnable.run();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}

			return false;
		});
		 */

		try
		{
			runnable.run();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public static File getFile(String path) throws IOException
	{
		Path path1 = KubeJS.getGameDirectory().resolve(path);
		KubeJS.verifyFilePath(path1);
		return path1.toFile();
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

	public static <T> FieldJS<T> getField(String className, String fieldName)
	{
		try
		{
			return getField(Class.forName(className), fieldName);
		}
		catch (Throwable ex)
		{
			return new FieldJS<>(null);
		}
	}

	public static <T> FieldJS<T> getField(Class className, String fieldName)
	{
		try
		{
			return new FieldJS<>(ObfuscationReflectionHelper.findField(className, fieldName));
		}
		catch (Throwable ex)
		{
			return new FieldJS<>(null);
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
	public static Stat<ResourceLocation> getStat(@Nullable Object id)
	{
		if (id == null)
		{
			return null;
		}
		else if (id instanceof Stat)
		{
			return (Stat) id;
		}

		return Stats.CUSTOM.get(getID(id));
	}

	public static ToolType getToolType(String id)
	{
		return ToolType.get(id);
	}

	public static WorldJS getWorld(IWorld world)
	{
		if (world.isRemote())
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

	@Nullable
	public static Effect getPotion(@Nullable Object id)
	{
		if (id == null)
		{
			return null;
		}
		else if (id instanceof Effect)
		{
			return (Effect) id;
		}

		return ForgeRegistries.POTIONS.getValue(getID(id));
	}

	public static ResourceLocation getID(@Nullable Object o)
	{
		if (o == null)
		{
			return NULL_ID;
		}
		else if (o instanceof ResourceLocation)
		{
			return (ResourceLocation) o;
		}

		return new ResourceLocation(o.toString());
	}

	public static String getNamespace(Object o)
	{
		return getID(o).getNamespace();
	}

	public static String getPath(Object o)
	{
		return getID(o).getPath();
	}
}
package dev.latvian.kubejs.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.world.ClientWorldJS;
import dev.latvian.kubejs.world.WorldJS;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.ScriptObject;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.EndNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NumberNBT;
import net.minecraft.nbt.StringNBT;
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
import java.util.Arrays;
import java.util.Map;
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

	@Nullable
	public static Object copy(@Nullable Object o)
	{
		if (o instanceof Copyable)
		{
			return ((Copyable) o).copy();
		}
		else if (o instanceof JsonElement)
		{
			return JsonUtilsJS.copy((JsonElement) o);
		}
		else if (o instanceof INBT)
		{
			return ((INBT) o).copy();
		}

		return o;
	}

	@Nullable
	public static Object normalize(@Nullable Object o)
	{
		//Primitives and already normalized objects
		if (o == null || o instanceof Normalized || o instanceof Number || o instanceof Character || o instanceof String || o.getClass().isPrimitive() && !o.getClass().isArray())
		{
			return o;
		}
		else if (o instanceof CharSequence)
		{
			return o.toString();
		}
		// New Nashorn JS Object
		else if (o instanceof JSObject)
		{
			JSObject js = (JSObject) o;

			if (js.isArray())
			{
				ListJS list = new ListJS(js.values().size());
				list.addAll(js.values());
				return list;
			}
			else
			{
				MapJS map = new MapJS();

				for (String k : ((JSObject) o).keySet())
				{
					map.put(k, ((JSObject) o).getMember(k));
				}

				return map;
			}
		}
		// Old Nashorn JS Object
		else if (o instanceof ScriptObject)
		{
			ScriptObject js = (ScriptObject) o;

			if (js.isArray())
			{
				ListJS list = new ListJS(js.size());
				list.addAll(js.values());
				return list;
			}
			else
			{
				MapJS map = new MapJS(js.size());

				for (Object k : js.keySet())
				{
					map.put(k.toString(), js.get(k));
				}

				return map;
			}
		}
		// Maps
		else if (o instanceof Map)
		{
			MapJS map = new MapJS(((Map) o).size());
			map.putAll((Map) o);
			return map;
		}
		// Lists, Collections, Iterables, GSON Arrays
		else if (o instanceof Iterable)
		{
			ListJS list = new ListJS();

			for (Object o1 : (Iterable) o)
			{
				list.add(o1);
			}

			return list;
		}
		// Arrays (and primitive arrays are a pain)
		else if (o.getClass().isArray())
		{
			ListJS list = new ListJS();

			if (o instanceof Object[])
			{
				list.addAll(Arrays.asList((Object[]) o));
			}
			else if (o instanceof int[])
			{
				for (int v : (int[]) o)
				{
					list.add(v);
				}
			}
			else if (o instanceof byte[])
			{
				for (byte v : (byte[]) o)
				{
					list.add(v);
				}
			}
			else if (o instanceof short[])
			{
				for (short v : (short[]) o)
				{
					list.add(v);
				}
			}
			else if (o instanceof long[])
			{
				for (long v : (long[]) o)
				{
					list.add(v);
				}
			}
			else if (o instanceof float[])
			{
				for (float v : (float[]) o)
				{
					list.add(v);
				}
			}
			else if (o instanceof double[])
			{
				for (double v : (double[]) o)
				{
					list.add(v);
				}
			}
			else if (o instanceof char[])
			{
				for (char v : (char[]) o)
				{
					list.add(v);
				}
			}

			return list;
		}
		// GSON Primitives
		else if (o instanceof JsonPrimitive)
		{
			JsonPrimitive p = (JsonPrimitive) o;

			if (p.isBoolean())
			{
				return p.getAsBoolean();
			}
			else if (p.isNumber())
			{
				return ((JsonPrimitive) o).getAsNumber();
			}

			return p.getAsString();
		}
		// GSON Objects
		else if (o instanceof JsonObject)
		{
			MapJS map = new MapJS(((JsonObject) o).size());

			for (Map.Entry<String, JsonElement> entry : ((JsonObject) o).entrySet())
			{
				map.put(entry.getKey(), entry.getValue());
			}

			return map;
		}
		// GSON and NBT Null
		else if (o instanceof JsonNull || o instanceof EndNBT)
		{
			return null;
		}
		// NBT
		else if (o instanceof CompoundNBT)
		{
			CompoundNBT nbt = (CompoundNBT) o;

			MapJS map = new MapJS(nbt.size());

			for (String s : nbt.keySet())
			{
				map.put(s, nbt.get(s));
			}

			return map;
		}
		else if (o instanceof NumberNBT)
		{
			return ((NumberNBT) o).getAsNumber();
		}
		else if (o instanceof StringNBT)
		{
			return ((StringNBT) o).getString();
		}

		return o;
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
		return ClientWorldJS.instance;
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
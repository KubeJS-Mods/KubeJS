package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.util.ConsoleJS;
import dev.latvian.kubejs.util.CountingMap;
import dev.latvian.kubejs.util.FieldJS;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.Overlay;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.world.ClientWorldJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.potion.Effect;
import net.minecraft.stats.Stat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.IWorld;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * @author LatvianModder
 */
public class UtilsWrapper
{
	public ServerJS getServer()
	{
		return ServerJS.instance;
	}

	public void queueIO(Runnable runnable)
	{
		UtilsJS.queueIO(runnable);
	}

	public Random getRandom()
	{
		return UtilsJS.RANDOM;
	}

	public <T> List<T> emptyList()
	{
		return Collections.emptyList();
	}

	public <K, V> Map<K, V> emptyMap()
	{
		return Collections.emptyMap();
	}

	public ListJS newList()
	{
		return new ListJS();
	}

	public Map newMap()
	{
		return new MapJS();
	}

	public CountingMap newCountingMap()
	{
		return new CountingMap();
	}

	public ResourceLocation id(String namespace, String path)
	{
		return new ResourceLocation(namespace, path);
	}

	public ResourceLocation id(Object id)
	{
		return UtilsJS.getID(id);
	}

	public ConsoleJS createConsole(String name)
	{
		return new ConsoleJS(ScriptType.STARTUP, LogManager.getLogger(name));
	}

	public Pattern regex(String pattern)
	{
		return Pattern.compile(pattern);
	}

	public Pattern regex(String pattern, int flags)
	{
		return Pattern.compile(pattern, flags);
	}

	public <T> FieldJS<T> getField(String className, String fieldName)
	{
		return UtilsJS.getField(className, fieldName);
	}

	public <T> FieldJS<T> getField(Class className, String fieldName)
	{
		return UtilsJS.getField(className, fieldName);
	}

	public int parseInt(@Nullable Object object, int def)
	{
		return UtilsJS.parseInt(object, def);
	}

	public double parseDouble(@Nullable Object object, double def)
	{
		return UtilsJS.parseDouble(object, def);
	}

	@Nullable
	public Stat<ResourceLocation> getStat(@Nullable Object id)
	{
		return UtilsJS.getStat(id);
	}

	public ToolType getToolType(String id)
	{
		return UtilsJS.getToolType(id);
	}

	public WorldJS getWorld(IWorld world)
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

	public WorldJS getClientWorld()
	{
		return ClientWorldJS.instance;
	}

	@Nullable
	public SoundEvent getSound(Object id)
	{
		return ForgeRegistries.SOUND_EVENTS.getValue(UtilsJS.getID(id));
	}

	public Object randomOf(Random random, Collection<Object> objects)
	{
		if (objects.isEmpty())
		{
			return null;
		}

		if (objects instanceof List)
		{
			return ((List) objects).get(random.nextInt(objects.size()));
		}
		else
		{
			return new ArrayList<>(objects).get(random.nextInt(objects.size()));
		}
	}

	public long getSystemTime()
	{
		return System.currentTimeMillis();
	}

	public Overlay overlay(String id, Object[] text)
	{
		Overlay o = new Overlay(id);

		for (Object o1 : text)
		{
			o.add(o1);
		}

		return o;
	}

	@Nullable
	public Effect getPotion(@Nullable Object id)
	{
		return UtilsJS.getPotion(id);
	}

	@Nullable
	public ListJS listOf(@Nullable Object o)
	{
		return ListJS.of(o);
	}

	public ListJS listOrSelf(@Nullable Object o)
	{
		return ListJS.orSelf(o);
	}

	@Nullable
	public MapJS mapOf(@Nullable Object o)
	{
		return MapJS.of(o);
	}

	@Nullable
	public Object copy(@Nullable Object o)
	{
		return UtilsJS.copy(o);
	}
}
package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.util.CountingMap;
import dev.latvian.kubejs.util.FieldJS;
import dev.latvian.kubejs.util.ID;
import dev.latvian.kubejs.util.LoggerWrapperJS;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.world.ClientWorldJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.stats.StatBase;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author LatvianModder
 */
public class UtilsWrapper
{
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

	public List newList()
	{
		return new ArrayList();
	}

	public Map newMap()
	{
		return new HashMap();
	}

	public Set newSet()
	{
		return new HashSet();
	}

	public CountingMap newCountingMap()
	{
		return new CountingMap();
	}

	public ID id(String namespace, String path)
	{
		return ID.of(namespace, path);
	}

	public ID id(Object id)
	{
		return ID.of(id);
	}

	public LoggerWrapperJS createLogger(String name)
	{
		return new LoggerWrapperJS(LogManager.getLogger(name));
	}

	public Pattern regex(String pattern)
	{
		return Pattern.compile(pattern);
	}

	public Pattern regex(String pattern, int flags)
	{
		return Pattern.compile(pattern, flags);
	}

	public FieldJS getField(String className, String fieldName)
	{
		return UtilsJS.getField(className, fieldName);
	}

	public FieldJS getField(String className, String fieldName, String obfFieldName)
	{
		return UtilsJS.getField(className, fieldName, obfFieldName);
	}

	public FieldJS getField(Class className, String fieldName)
	{
		return UtilsJS.getField(className, fieldName);
	}

	public FieldJS getField(Class className, String fieldName, String obfFieldName)
	{
		return UtilsJS.getField(className, fieldName, obfFieldName);
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
	public StatBase getStat(@Nullable Object id)
	{
		return UtilsJS.getStat(id);
	}

	public String getToolType(String id)
	{
		return UtilsJS.getToolType(id);
	}

	public WorldJS getWorld(World world)
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

	public WorldJS getClientWorld()
	{
		return ClientWorldJS.get();
	}

	@Nullable
	public SoundEvent getSound(Object id)
	{
		return ForgeRegistries.SOUND_EVENTS.getValue(ID.of(id).mc());
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
}
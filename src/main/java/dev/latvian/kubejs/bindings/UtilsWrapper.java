package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.server.ServerJS;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author LatvianModder
 */
public class UtilsWrapper
{
	public <T> List<T> emptyList()
	{
		return Collections.emptyList();
	}

	public <K, V> Map<K, V> emptyMap()
	{
		return Collections.emptyMap();
	}

	public ID id(String namespace, String path)
	{
		return new ID(namespace, path);
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

	public FieldJS getField(Class className, String fieldName)
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
}
package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.util.FieldJS;
import dev.latvian.kubejs.util.ID;
import dev.latvian.kubejs.util.LoggerWrapperJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.stats.StatBase;
import net.minecraft.util.SoundEvent;
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
		return id instanceof ID ? (ID) id : new ID(String.valueOf(id));
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

	public static FieldJS field(String className, String fieldName)
	{
		return UtilsJS.field(className, fieldName);
	}

	public static FieldJS field(Class className, String fieldName)
	{
		return UtilsJS.field(className, fieldName);
	}

	public static int parseInt(@Nullable Object object, int def)
	{
		return UtilsJS.parseInt(object, def);
	}

	public static double parseDouble(@Nullable Object object, double def)
	{
		return UtilsJS.parseDouble(object, def);
	}

	@Nullable
	public StatBase stat(@Nullable Object id)
	{
		return UtilsJS.stat(id);
	}

	public static String toolType(String id)
	{
		return UtilsJS.toolType(id);
	}

	@Nullable
	public SoundEvent sound(Object id)
	{
		return ForgeRegistries.SOUND_EVENTS.getValue(new ID(id).mc());
	}
}
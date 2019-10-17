package dev.latvian.kubejs.documentation;

import dev.latvian.kubejs.script.DataType;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class Documentation
{
	private static final HashSet<String> OBJECT_METHODS_0P = new HashSet<>(Arrays.asList("toString", "wait", "equals", "hashCode", "notify", "notifyAll", "getClass"));
	private static final HashSet<String> OBJECT_METHODS_1P = new HashSet<>(Collections.singletonList("equals"));

	public static boolean isObjectMethod(String n, int p)
	{
		if (p == 0)
		{
			return OBJECT_METHODS_0P.contains(n);
		}
		else if (p == 1)
		{
			return OBJECT_METHODS_1P.contains(n);
		}

		return false;
	}

	private static Documentation instance;

	public static Documentation get()
	{
		if (instance == null)
		{
			instance = new Documentation();
			instance.init();
		}

		return instance;
	}

	public static void clearCache()
	{
		instance = null;
	}

	public Map<Class, String> customNames;
	public Map<String, DocumentedEvent> events;
	public Map<Class, DocumentedEvent> classToEvent;
	public Map<Class, Map<String, Class>> attachedData;

	public void init()
	{
		customNames = new LinkedHashMap<>();
		events = new LinkedHashMap<>();
		classToEvent = new HashMap<>();
		attachedData = new HashMap<>();
		MinecraftForge.EVENT_BUS.post(new DocumentationEvent(this));
	}

	public void registerAttachedData(DataType type, String name, Class dataClass)
	{
		attachedData.computeIfAbsent(type.actualParent, k -> new LinkedHashMap<>()).put(name, dataClass);
	}

	public void registerCustomName(String name, Class... classes)
	{
		for (Class c : classes)
		{
			customNames.put(c, name);
		}
	}

	public void registerEvent(DocumentedEvent e)
	{
		events.put(e.eventID, e);
		classToEvent.put(e.eventClass, e);

		if (!e.doubleParam.isEmpty())
		{
			events.put(e.eventID + ".<" + e.doubleParam + ">", e);
		}
	}

	public String getPrettyName(Class c)
	{
		if (c.isArray())
		{
			return getPrettyName(c.getComponentType()) + "[]";
		}

		String s = customNames.get(c);

		if (s != null)
		{
			return s;
		}

		DisplayName displayName = (DisplayName) c.getAnnotation(DisplayName.class);

		if (displayName != null && !displayName.value().isEmpty())
		{
			return displayName.value();
		}

		String[] s1 = c.getName().split("\\.");

		if (s1.length == 3 && s1[0].equals("java") && (s1[1].equals("lang") || s1[1].equals("util")))
		{
			return s1[2];
		}

		String name = s1[s1.length - 1];

		if (c.isInterface() && name.length() >= 2 && name.charAt(0) == 'I' && Character.isUpperCase(name.charAt(1)))
		{
			name = name.substring(1);
		}

		if (name.endsWith("JS"))
		{
			name = name.substring(0, name.length() - 2);
		}

		return name;
	}

	@Nullable
	public Class getActualType(@Nullable Class c)
	{
		if (c == null)
		{
			return null;
		}

		if (c.isArray())
		{
			return getActualType(c.getComponentType());
		}

		return c;
	}
}
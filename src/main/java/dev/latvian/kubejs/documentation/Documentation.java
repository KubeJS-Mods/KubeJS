package dev.latvian.kubejs.documentation;

import dev.latvian.kubejs.command.CommandSender;
import dev.latvian.kubejs.script.DataType;
import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.text.TextString;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class Documentation
{
	public static final HashSet<String> OBJECT_METHODS = new HashSet<>(Arrays.asList("toString", "wait", "equals", "hashCode", "notify", "notifyAll", "getClass"));

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

	private Map<Class, String> customNames;
	private Map<String, DocumentedEvent> events;
	private Map<Class, DocumentedEvent> classToEvent;
	private Map<Class, Map<String, Class>> attachedData;

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

	public String getSimpleName(Class c)
	{
		String[] s = c.getName().split("\\.");
		return s[s.length - 1];
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

		DocClass docClass = (DocClass) c.getAnnotation(DocClass.class);

		if (docClass != null && !docClass.displayName().isEmpty())
		{
			return docClass.displayName();
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

		List<String> list = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		boolean prevUppercase = true;

		for (int i = 0; i < name.length(); i++)
		{
			char ch = name.charAt(i);

			boolean uppercase = Character.isUpperCase(ch);

			if (uppercase && !prevUppercase)
			{
				if (sb.length() > 0)
				{
					list.add(sb.toString());
				}

				sb.setLength(0);
			}

			prevUppercase = uppercase;
			sb.append(ch);
		}

		if (sb.length() > 0)
		{
			list.add(sb.toString());
		}

		return String.join(" ", list);
	}

	public Text classText(Class c, Type t)
	{
		Text text = new TextString(getPrettyName(c)).yellow();

		if (c.isPrimitive() || c == Character.class || Number.class.isAssignableFrom(c))
		{
			return text;
		}

		text.hover("<More Info>").click("command:/kubejs docs " + c.getName());

		if (t instanceof ParameterizedType)
		{
			text.append("<");

			Type[] at = ((ParameterizedType) t).getActualTypeArguments();

			for (int i = 0; i < at.length; i++)
			{
				if (i > 0)
				{
					text.append(", ");
				}

				if (at[i] instanceof Class)
				{
					text.append(classText((Class) at[i], at[i]));
				}
				else
				{
					text.append("?");
				}
			}

			text.append(">");
		}

		return text;
	}

	public void sendDocs(CommandSender sender)
	{
		sender.tell(new TextString("== KubeJS Documentation ==").darkPurple().hover("This is WIP, it will look much better later"));
		sender.tell(new TextString("[Global]").blue());

		for (Map.Entry<String, Object> entry : ScriptManager.instance.bindings.entrySet())
		{
			Text name = new TextString(entry.getKey()).green();

			if (entry.getKey().toUpperCase().equals(entry.getKey()))
			{
				if (entry.getValue() instanceof IStringSerializable)
				{
					name.hover(((IStringSerializable) entry.getValue()).getName());
				}
				else if (entry.getValue() instanceof CharSequence)
				{
					name.hover("\"" + entry.getValue() + "\"");
				}
				else
				{
					name.hover(entry.getValue().toString());
				}
			}

			sender.tell((classText(entry.getValue().getClass(), entry.getValue().getClass())).append(" ").append(name));
		}

		sender.tell(new TextString("[Events]").blue());

		List<String> list = new ArrayList<>(events.keySet());
		list.sort(null);

		for (String s : list)
		{
			sender.tell(new TextString(s).yellow().hover("<More Info>").click("command:/kubejs docs " + events.get(s).eventClass.getName()));
		}
	}

	public void sendDocs(CommandSender sender, Class c)
	{
		DocClass docClass = (DocClass) c.getAnnotation(DocClass.class);

		sender.tell(new TextString("").append("== ").append(new TextString(getPrettyName(c)).lightPurple()).append(" ==").darkPurple().hover("This is WIP, it will look much better later"));

		if (docClass != null && !docClass.value().isEmpty())
		{
			sender.tell(new TextString(docClass.value()).italic().gray());
		}

		sender.tell(new TextString("[Class]").blue());
		sender.tell(new TextString(c.getName()).yellow());

		Class sc = c.getSuperclass();

		sender.tell(new TextString("[Extends]").blue());
		boolean has = false;

		if (sc != null && sc != Object.class)
		{
			sender.tell(classText(sc, sc));
			has = true;
		}

		for (Class c1 : c.getInterfaces())
		{
			sender.tell(classText(c1, c1));
			has = true;
		}

		if (!has)
		{
			sender.tell("<None>");
		}

		DocumentedEvent event = classToEvent.get(c);

		if (event != null)
		{
			sender.tell(new TextString("[Event]").blue());
			sender.tell("ID: " + event.eventID);

			if (!event.doubleParam.isEmpty())
			{
				sender.tell("Alt ID: " + event.eventID + ".<" + event.doubleParam + ">");
			}

			sender.tell("Can cancel: " + event.canCancel);
			sender.tell("Sides: " + (event.sideOnly == null ? "[Server, Client]" : event.sideOnly == Side.CLIENT ? "[Client]" : "[Server]"));
		}

		sender.tell(new TextString("[Fields]").blue());
		has = false;

		for (Field field : c.getDeclaredFields())
		{
			int m = field.getModifiers();

			if (Modifier.isPublic(m) && !Modifier.isTransient(m) && !Modifier.isStatic(m))
			{
				DocField docField = field.getAnnotation(DocField.class);

				if (docField == null && docClass != null)
				{
					continue;
				}

				Text text = new TextString("").append(classText(field.getType(), field.getGenericType())).append(" ").append(new TextString(field.getName()).green());

				if (docField != null && !docField.value().isEmpty())
				{
					text.hover(docField.value());
				}

				sender.tell(text);
				has = true;
			}
		}

		if (!has)
		{
			sender.tell("<None>");
		}

		sender.tell(new TextString("[Methods]").blue());
		has = false;

		for (Method method : c.getDeclaredMethods())
		{
			int m = method.getModifiers();

			if (Modifier.isPublic(m) && !Modifier.isStatic(m))
			{
				DocMethod docMethod = method.getAnnotation(DocMethod.class);

				if (docMethod == null && docClass != null)
				{
					continue;
				}

				String mn = method.getName();

				if (docMethod == null && OBJECT_METHODS.contains(mn))
				{
					continue;
				}

				Parameter[] parameters = method.getParameters();

				Text text = new TextString("").append(classText(method.getReturnType(), method.getGenericReturnType())).append(" ");
				Text namet = new TextString(method.getName()).green();

				if (docMethod != null && !docMethod.value().isEmpty())
				{
					namet.hover(docMethod.value());
				}

				text.append(namet).append("(");

				Param[] params = docMethod == null ? new Param[0] : docMethod.params();

				for (int i = 0; i < parameters.length; i++)
				{
					Parameter p = parameters[i];

					String info = "";
					String name = "";
					Class type = Object.class;

					if (params.length == parameters.length)
					{
						type = params[i].type();
						info = params[i].info();
						name = params[i].value();
					}

					if (type == Object.class)
					{
						type = p.getType();
					}

					if (name.isEmpty())
					{
						if (parameters.length == 1)
						{
							name = getPrettyName(type).substring(0, 1).toLowerCase();
						}
						else
						{
							name = p.getName();
						}
					}

					if (i > 0)
					{
						text.append(", ");
					}

					Text text1 = new TextString("").append(classText(type, type)).append(" ").append(name);

					if (!info.isEmpty())
					{
						text1.hover(info);
					}

					text.append(text1);
				}

				sender.tell(text.append(")"));
				has = true;
			}
		}

		if (!has)
		{
			sender.tell("<None>");
		}

		Map<String, Class> attached = attachedData.get(c);

		if (attached != null && !attached.isEmpty())
		{
			sender.tell(new TextString("[Attached]").blue());

			for (Map.Entry<String, Class> entry : attached.entrySet())
			{
				sender.tell(new TextString("").append(classText(entry.getValue(), entry.getValue())).append(" ").append(new TextString(entry.getKey()).green()));
			}
		}
	}
}
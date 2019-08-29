package dev.latvian.kubejs.documentation;

import dev.latvian.kubejs.command.CommandSender;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.text.TextString;
import net.minecraftforge.common.MinecraftForge;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author LatvianModder
 */
public enum Documentation
{
	INSTANCE;

	private Map<Class, String> nativeClasses;
	private Set<Class> registeredClasses;
	private Map<String, Class<? extends EventJS>> registeredEvents;

	public void init()
	{
		clearCache();
		nativeClasses = new LinkedHashMap<>();
		registeredClasses = new LinkedHashSet<>();
		registeredEvents = new LinkedHashMap<>();
		MinecraftForge.EVENT_BUS.post(new DocumentationEvent(this));
	}

	public void clearCache()
	{
	}

	public void registerNative(String name, Class... classes)
	{
		for (Class c : classes)
		{
			nativeClasses.put(c, name);
		}

		clearCache();
	}

	public void register(Class c)
	{
		registeredClasses.add(c);
		clearCache();
	}

	public void registerEvent(String id, Class<? extends EventJS> event)
	{
		registeredEvents.put(id, event);
		clearCache();
	}

	public String getSimpleName(Class c)
	{
		String[] s = c.getName().split("\\.");
		return s[s.length - 1];
	}

	public String getPrettyName(Class c)
	{
		String s = nativeClasses.get(c);

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

		for (int i = 0; i < name.length(); i++)
		{
			char ch = name.charAt(i);

			if (Character.isUpperCase(ch))
			{
				if (sb.length() > 0)
				{
					list.add(sb.toString());
				}

				sb.setLength(0);
			}

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

		if (nativeClasses.containsKey(c))
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
		sender.tell(new TextString("[Native Types]").blue());
		List<String> nativeTypeList = new ArrayList<>(new HashSet<>(nativeClasses.values()));
		nativeTypeList.sort(null);

		for (String s : nativeTypeList)
		{
			sender.tell(new TextString(s).yellow());
		}

		sender.tell(new TextString("[Classes]").blue());
		HashSet<Class> classSet = new HashSet<>(registeredClasses);
		classSet.addAll(registeredEvents.values());
		List<Class> classList = new ArrayList<>(classSet);
		classList.sort((o1, o2) -> getPrettyName(o1).compareToIgnoreCase(getPrettyName(o2)));

		for (Class c : classList)
		{
			//if (c.isAnnotationPresent(DocClass.class))
			{
				sender.tell(classText(c, c));
			}
		}

		sender.tell(new TextString("[Events]").blue());

		List<String> list = new ArrayList<>(registeredEvents.keySet());
		list.sort(null);

		for (String s : list)
		{
			sender.tell(new TextString(s).yellow().hover("<More Info>").click("command:/kubejs docs " + registeredEvents.get(s).getName()));
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

				Text text = new TextString("").append(classText(method.getReturnType(), method.getGenericReturnType())).append(" ");
				Text namet = new TextString(method.getName()).green();

				if (docMethod != null && !docMethod.value().isEmpty())
				{
					namet.hover(docMethod.value());
				}

				text.append(namet).append("(");

				Parameter[] parameters = method.getParameters();
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
						name = p.getName();
					}

					if (i > 0)
					{
						text.append("");
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
	}
}
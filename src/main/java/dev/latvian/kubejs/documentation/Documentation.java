package dev.latvian.kubejs.documentation;

import dev.latvian.kubejs.documentation.tags.PairedTag;
import dev.latvian.kubejs.documentation.tags.Tag;
import dev.latvian.kubejs.script.DataType;
import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.kubejs.script.ScriptModData;
import io.netty.handler.codec.http.FullHttpRequest;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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

	public String handleHTTP(FullHttpRequest request)
	{
		Tag html = new PairedTag("body", "");

		Tag head = html.paired("head", "");
		head.paired("title", "KubeJS Documentation");
		head.unpaired("link").attr("rel", "stylesheet").attr("type", "text/css").attr("href", "https://kubejs.latvian.dev/style.css");
		head.unpaired("link").attr("rel", "icon").attr("href", "https://kubejs.latvian.dev/logo_48.png");

		Tag body = html.paired("body", "");
		body.img("https://kubejs.latvian.dev/logo_title.png").style("height", "7em");
		body.br();
		body.h1("").a("KubeJS Documentation", "/");

		if (request.uri().equals("/"))
		{
			showHome(body);
		}
		else
		{
			try
			{
				showClass(body, Class.forName(request.uri().substring(1)));
			}
			catch (Exception ex)
			{
				body.h1("Error!");
				Tag t = body.p();
				t.text("Class ");
				t.span(request.uri().substring(1), "type");
				t.text(" not found!");
			}
		}

		body.br();
		body.p().paired("i", "Hosted from '" + FMLCommonHandler.instance().getMinecraftServerInstance().getMOTD() + "'");
		body.p().paired("i", "Mod version: " + ScriptModData.getInstance().getModVersion());
		body.p().paired("i", "Mod loader: " + ScriptModData.getInstance().getType());
		body.p().paired("i", "Minecraft version: " + ScriptModData.getInstance().getMcVersion());
		body.p().paired("i").a("Visit kubejs.latvian.dev for more info about the mod", "https://kubejs.latvian.dev");

		StringBuilder builder = new StringBuilder("<!DOCTYPE html>");
		html.build(builder);
		return builder.toString();
	}

	private Class getActualType(Class c)
	{
		if (c.isArray())
		{
			return getActualType(c.getComponentType());
		}

		return c;
	}

	public void classText(Tag parent, Class c, Type t)
	{
		Class ac = getActualType(c);

		if (ac.isPrimitive() || ac == Character.class || Number.class.isAssignableFrom(ac))
		{
			parent.span(getPrettyName(c), "type");
			return;
		}

		Tag tag = parent.span("", "");
		tag.a(getPrettyName(c), "/" + ac.getName()).addClass("type").title("More Info");

		if (t instanceof ParameterizedType)
		{
			tag.text("<");

			Type[] at = ((ParameterizedType) t).getActualTypeArguments();

			for (int i = 0; i < at.length; i++)
			{
				if (i > 0)
				{
					tag.text(", ");
				}

				if (at[i] instanceof Class)
				{
					classText(tag, (Class) at[i], at[i]);
				}
				else
				{
					tag.span(at[i].getTypeName(), "type");
				}
			}

			tag.text(">");
		}
	}

	public void classText(Tag parent, Class c)
	{
		classText(parent, c, c);
	}

	public void yesNoSpan(Tag parent, boolean value)
	{
		parent.span(value ? "Yes" : "No", value ? "yes" : "no");
	}

	private void showHome(Tag body)
	{
		List<DocumentedBinding> globalList = new ArrayList<>();
		List<DocumentedBinding> constantList = new ArrayList<>();

		for (Map.Entry<String, Object> entry : ScriptManager.instance.bindings.entrySet())
		{
			globalList.add(new DocumentedBinding(entry.getKey(), entry.getValue().getClass(), ""));
		}

		for (Map.Entry<String, Object> entry : ScriptManager.instance.constants.entrySet())
		{
			String value;

			if (entry.getValue() instanceof Enum)
			{
				value = "Enum Constant";
			}
			else if (entry.getValue() instanceof CharSequence)
			{
				value = "\"" + entry.getValue() + "\"";
			}
			else if (entry.getValue() instanceof Number)
			{
				value = entry.getValue().toString();
			}
			else if (entry.getValue() instanceof IForgeRegistryEntry)
			{
				value = ((IForgeRegistryEntry) entry.getValue()).getRegistryName().toString();
			}
			else
			{
				value = "Object Constant";
			}

			if (value.isEmpty())
			{
				globalList.add(new DocumentedBinding(entry.getKey(), entry.getValue().getClass(), ""));
			}
			else
			{
				constantList.add(new DocumentedBinding(entry.getKey(), entry.getValue().getClass(), value));
			}
		}

		globalList.sort(null);
		constantList.sort(null);

		body.h2("Global").id("global");
		Tag globalTable = body.table().addClass("doc");
		Tag gtTopRow = globalTable.tr();
		gtTopRow.th().text("Name");
		gtTopRow.th().text("Type");

		for (DocumentedBinding object : globalList)
		{
			Tag row = globalTable.tr();
			row.td().text(object.name);
			classText(row.td(), object.type);
		}

		body.h2("Constants").id("constants");
		Tag constantTable = body.table().addClass("doc");
		Tag ctTopRow = constantTable.tr();
		ctTopRow.th().text("Name");
		ctTopRow.th().text("Type");
		ctTopRow.th().text("Value");

		for (DocumentedBinding object : constantList)
		{
			Tag row = constantTable.tr();
			row.td().text(object.name);
			classText(row.td(), object.type);
			row.td().text(object.value);
		}

		body.h2("Events").id("events");

		List<DocumentedEvent> list = new ArrayList<>(events.values());
		list.sort(null);

		Tag eventTable = body.paired("table", "").addClass("doc");
		Tag topRow = eventTable.tr();
		topRow.th().text("ID").title("ID of this event. For double events, alternate ID will be provided");
		topRow.th().text("Type");
		topRow.th().text("Can cancel").title("True if event can be cancelled");
		topRow.th().text("Client").title("True if event is fired on client side");
		topRow.th().text("Server").title("True if event is fired on server side");

		for (DocumentedEvent event : list)
		{
			Tag row = eventTable.tr();
			row.td().text(event.eventID);
			classText(row.td(), event.eventClass);
			yesNoSpan(row.td(), event.canCancel);
			yesNoSpan(row.td(), event.sideOnly == null || event.sideOnly == Side.CLIENT);
			yesNoSpan(row.td(), event.sideOnly == null || event.sideOnly == Side.SERVER);
		}
	}

	private void showClass(Tag body, Class c)
	{
		body.h2(getPrettyName(c));

		DisplayName docClass = (DisplayName) c.getAnnotation(DisplayName.class);

		if (docClass != null && !docClass.value().isEmpty())
		{
			body.p(docClass.value());
		}

		body.h3(c.isInterface() ? "Interface" : "Class");
		body.p(c.getName()).addClass("type");

		Class sc = c.getSuperclass();

		body.h3("Extends");

		boolean has = false;

		if (sc != null && sc != Object.class)
		{
			classText(body.p(), sc);
			has = true;
		}

		for (Class c1 : c.getInterfaces())
		{
			classText(body.p(), c1);
			has = true;
		}

		if (!has)
		{
			body.text("<None>");
		}

		DocumentedEvent event = classToEvent.get(c);

		if (event != null)
		{
			body.h3("Event");
			Tag table = body.paired("table", "").addClass("doc");
			Tag topRow = table.tr();
			topRow.th().text("ID").title("ID of this event. For double events, alternate ID will be provided");
			topRow.th().text("Can cancel").title("True if event can be cancelled");
			topRow.th().text("Client").title("True if event is fired on client side");
			topRow.th().text("Server").title("True if event is fired on server side");
			Tag row = table.tr();
			row.td().text(event.eventID);
			yesNoSpan(row.td(), event.canCancel);
			yesNoSpan(row.td(), event.sideOnly == null || event.sideOnly == Side.CLIENT);
			yesNoSpan(row.td(), event.sideOnly == null || event.sideOnly == Side.SERVER);
		}

		Map<String, Class> attached = attachedData.get(c);

		if (attached != null && !attached.isEmpty())
		{
			body.h3("Attached");

			Tag attachedTable = body.table().addClass("doc");
			Tag atTopRow = attachedTable.tr();
			atTopRow.th().text("Name");
			atTopRow.th().text("Type");

			List<Map.Entry<String, Class>> list = new ArrayList<>(attached.entrySet());
			list.sort(Comparator.comparing(Map.Entry::getKey));

			for (Map.Entry<String, Class> entry : list)
			{
				Tag row = attachedTable.tr();
				row.td().text(entry.getKey());
				classText(row.td(), entry.getValue());
			}
		}

		List<DocumentedField> fieldList = new ArrayList<>();

		for (Field field : c.getFields())
		//for (Field field : c.getDeclaredFields())
		{
			int m = field.getModifiers();

			if (Modifier.isPublic(m) && !Modifier.isStatic(m) && !field.isAnnotationPresent(Ignore.class))
			{
				fieldList.add(new DocumentedField(this, field));
			}
		}

		body.h3("Fields").id("fields");

		if (!fieldList.isEmpty())
		{
			fieldList.sort(null);

			Tag methodTable = body.table().addClass("doc");
			Tag mtTopRow = methodTable.tr();
			mtTopRow.th().text("Name");
			mtTopRow.th().text("Return Type");
			mtTopRow.th().text("Info");

			for (DocumentedField field : fieldList)
			{
				Tag row = methodTable.tr();

				Tag n = row.td().span("", "");
				n.text(field.name);
				classText(row.td(), field.type, field.actualType);
				row.td().text(field.info);
			}
		}
		else
		{
			body.p("<None>");
		}

		body.h3("Methods").id("methods");

		List<DocumentedMethod> methodList = new ArrayList<>();

		for (Method method : c.getMethods())
		//for (Method method : c.getDeclaredMethods())
		{
			int m = method.getModifiers();

			if (Modifier.isPublic(m) && !Modifier.isStatic(m) && !method.isAnnotationPresent(Ignore.class))
			{
				if (!OBJECT_METHODS.contains(method.getName()))
				{
					methodList.add(new DocumentedMethod(this, method));
				}
			}
		}

		if (!methodList.isEmpty())
		{
			methodList.sort(null);

			Tag methodTable = body.table().addClass("doc");
			Tag mtTopRow = methodTable.tr();
			mtTopRow.th().text("Name");
			mtTopRow.th().text("Return Type");
			mtTopRow.th().text("Bean");
			mtTopRow.th().text("Info");

			for (DocumentedMethod method : methodList)
			{
				Tag row = methodTable.tr();

				Tag n = row.td().span("", "");
				n.text(method.name);
				n.text("(");

				for (int i = 0; i < method.paramNames.length; i++)
				{
					if (i > 0)
					{
						n.text(", ");
					}

					classText(n, method.paramTypes[i], method.actualParamTypes[i]);
					n.text(" ");
					n.text(method.paramNames[i]);
				}

				n.text(")");

				classText(row.td(), method.returnType, method.actualReturnType);

				if (method.paramNames.length == 0 && method.name.length() > 3 && method.name.startsWith("get"))
				{
					row.td().text(method.name.substring(3, 4).toLowerCase() + method.name.substring(4));
				}
				else if (method.paramNames.length == 0 && method.name.length() > 2 && method.name.startsWith("is"))
				{
					row.td().text(method.name.substring(2, 3).toLowerCase() + method.name.substring(3));
				}
				else if (method.paramNames.length == 1 && method.name.length() > 3 && method.name.startsWith("set"))
				{
					row.td().text(method.name.substring(3, 4).toLowerCase() + method.name.substring(4));
				}
				else
				{
					row.td().text("-");
				}

				row.td().text(method.info);
			}
		}
		else
		{
			body.p("<None>");
		}
	}
}
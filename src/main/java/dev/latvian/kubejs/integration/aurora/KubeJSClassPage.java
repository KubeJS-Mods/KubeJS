package dev.latvian.kubejs.integration.aurora;

import dev.latvian.kubejs.documentation.DisplayName;
import dev.latvian.kubejs.documentation.Documentation;
import dev.latvian.kubejs.documentation.DocumentedEvent;
import dev.latvian.kubejs.documentation.DocumentedField;
import dev.latvian.kubejs.documentation.DocumentedMethod;
import dev.latvian.kubejs.documentation.Ignore;
import dev.latvian.kubejs.event.EventsJS;
import dev.latvian.kubejs.script.ScriptModData;
import dev.latvian.mods.aurora.page.HTTPWebPage;
import dev.latvian.mods.aurora.tag.Tag;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class KubeJSClassPage extends HTTPWebPage
{
	private final Documentation documentation;
	private final Class documentedClass;

	public KubeJSClassPage(Documentation d, Class c)
	{
		documentation = d;
		documentedClass = c;
	}

	@Override
	public String getTitle()
	{
		return "KubeJS Documentation";
	}

	@Override
	public String getDescription()
	{
		return documentedClass.getName();
	}

	@Override
	public String getIcon()
	{
		return "https://kubejs.latvian.dev/logo_48.png";
	}

	@Override
	public String getStylesheet()
	{
		return "https://kubejs.latvian.dev/style.css";
	}

	@Override
	public void body(Tag body)
	{
		body.img("https://kubejs.latvian.dev/logo_title.png").style("height", "7em");
		body.br();
		body.h1("").a("KubeJS Documentation", "/kubejs");

		Class c = documentedClass;
		body.h2(documentation.getPrettyName(c));

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
			KubeJSHomePage.classText(documentation, body.p(), sc);
			has = true;
		}

		for (Class c1 : c.getInterfaces())
		{
			KubeJSHomePage.classText(documentation, body.p(), c1);
			has = true;
		}

		if (!has)
		{
			body.text("<None>");
		}

		DocumentedEvent event = documentation.classToEvent.get(c);

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
			KubeJSHomePage.yesNoSpan(row.td(), event.canCancel);
			KubeJSHomePage.yesNoSpan(row.td(), event.sideOnly == null || event.sideOnly == Side.CLIENT);
			KubeJSHomePage.yesNoSpan(row.td(), event.sideOnly == null || event.sideOnly == Side.SERVER);
		}

		Map<String, Class> attached = documentation.attachedData.get(c);

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
				KubeJSHomePage.classText(documentation, row.td(), entry.getValue());
			}
		}

		List<DocumentedField> fieldList = new ArrayList<>();

		for (Field field : c.getFields())
		//for (Field field : c.getDeclaredFields())
		{
			int m = field.getModifiers();

			if (Modifier.isPublic(m) && !Modifier.isStatic(m) && !field.isAnnotationPresent(Ignore.class))
			{
				fieldList.add(new DocumentedField(documentation, field));
			}
		}

		if (EventsJS.class.isAssignableFrom(c))
		{
			body.h3("Fields").id("fields");

			if (!fieldList.isEmpty())
			{
				fieldList.sort(null);

				boolean info = false;

				for (DocumentedField field : fieldList)
				{
					if (!field.info.isEmpty())
					{
						info = true;
						break;
					}
				}

				Tag methodTable = body.table().addClass("doc");
				Tag mtTopRow = methodTable.tr();
				mtTopRow.th().text("Name");
				mtTopRow.th().text("Return Type");

				if (info)
				{
					mtTopRow.th().text("Info");
				}

				for (DocumentedField field : fieldList)
				{
					Tag row = methodTable.tr();

					Tag n = row.td().span("", "");
					n.text(field.name);
					KubeJSHomePage.classText(documentation, row.td(), field.type, field.actualType);

					if (info)
					{
						row.td().text(field.info);
					}
				}
			}
			else
			{
				body.p("<None>");
			}
		}

		body.h3("Methods").id("methods");

		List<DocumentedMethod> methodList = new ArrayList<>();

		for (Method method : c.getMethods())
		//for (Method method : c.getDeclaredMethods())
		{
			int m = method.getModifiers();

			if (Modifier.isPublic(m) && !Modifier.isStatic(m) && !method.isAnnotationPresent(Ignore.class))
			{
				if (!Documentation.OBJECT_METHODS.contains(method.getName()))
				{
					methodList.add(new DocumentedMethod(documentation, method));
				}
			}
		}

		if (!methodList.isEmpty())
		{
			methodList.sort(null);

			boolean info = false;

			for (DocumentedMethod method : methodList)
			{
				if (!method.info.isEmpty())
				{
					info = true;
					break;
				}
			}

			Tag methodTable = body.table().addClass("doc");
			Tag mtTopRow = methodTable.tr();
			mtTopRow.th().text("Name");
			mtTopRow.th().text("Return Type");
			mtTopRow.th().text("Bean");

			if (info)
			{
				mtTopRow.th().text("Info");
			}

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

					KubeJSHomePage.classText(documentation, n, method.paramTypes[i], method.actualParamTypes[i]);
					n.text(" ");
					n.text(method.paramNames[i]);
				}

				n.text(")");

				KubeJSHomePage.classText(documentation, row.td(), method.returnType, method.actualReturnType);

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

				if (info)
				{
					row.td().text(method.info);
				}
			}
		}
		else
		{
			body.p("<None>");
		}

		body.br();
		body.p().paired("i", "Hosted from '" + FMLCommonHandler.instance().getMinecraftServerInstance().getMOTD() + "'");
		body.p().paired("i", "Mod version: " + ScriptModData.getInstance().getModVersion());
		body.p().paired("i", "Mod loader: " + ScriptModData.getInstance().getType());
		body.p().paired("i", "Minecraft version: " + ScriptModData.getInstance().getMcVersion());
		body.p().paired("i").a("Visit kubejs.latvian.dev for more info about the mod", "https://kubejs.latvian.dev");
	}
}
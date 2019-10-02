package dev.latvian.kubejs.integration.aurora;

import dev.latvian.kubejs.documentation.DisplayName;
import dev.latvian.kubejs.documentation.Documentation;
import dev.latvian.kubejs.documentation.DocumentedEvent;
import dev.latvian.kubejs.documentation.DocumentedField;
import dev.latvian.kubejs.documentation.DocumentedMethod;
import dev.latvian.kubejs.documentation.Ignore;
import dev.latvian.kubejs.event.EventJS;
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
import java.util.LinkedHashMap;
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

		Tag classTable = body.table().id("classname").addClass("doc");
		classTable.tr().th().a(c.isInterface() ? "Interface" : "Class", "#classname");
		classTable.tr().td().span(c.getName(), "type");
		body.br();

		Class sc = c.getSuperclass();

		Tag extendsTable = body.table().id("extends").addClass("doc");
		extendsTable.tr().th().a("Extends", "#extends");

		if (sc != null && sc != Object.class)
		{
			KubeJSHomePage.classText(documentation, extendsTable.tr().td(), sc);
		}

		for (Class c1 : c.getInterfaces())
		{
			KubeJSHomePage.classText(documentation, extendsTable.tr().td(), c1);
		}

		body.br();

		DocumentedEvent event = documentation.classToEvent.get(c);

		if (event != null)
		{
			Tag table = body.paired("table", "").addClass("doc");
			Tag topRow = table.tr();
			KubeJSHomePage.hover(topRow.th().a("Event", "#event"), "ID of this event. For double events, alternate ID will be provided");
			KubeJSHomePage.hover(topRow.th().text("Can cancel"), "True if event can be cancelled");
			KubeJSHomePage.hover(topRow.th().text("Client"), "True if event is fired on client side");
			KubeJSHomePage.hover(topRow.th().text("Server"), "True if event is fired on server side");
			Tag row = table.tr();
			row.td().text(event.eventID);
			KubeJSHomePage.yesNoSpan(row.td(), event.canCancel);
			KubeJSHomePage.yesNoSpan(row.td(), event.sideOnly == null || event.sideOnly == Side.CLIENT);
			KubeJSHomePage.yesNoSpan(row.td(), event.sideOnly == null || event.sideOnly == Side.SERVER);
			body.br();
		}

		Map<String, Class> attached = documentation.attachedData.get(c);

		if (attached != null && !attached.isEmpty())
		{
			Tag attachedTable = body.table().addClass("doc");
			attachedTable.tr().th().a("Attached", "#attached").id("attached");

			List<Map.Entry<String, Class>> list = new ArrayList<>(attached.entrySet());
			list.sort(Comparator.comparing(Map.Entry::getKey));

			for (Map.Entry<String, Class> entry : list)
			{
				Tag row = attachedTable.tr().td();
				KubeJSHomePage.classText(documentation, row, entry.getValue());
				row.text(" ");
				row.text(entry.getKey());
			}

			body.br();
		}

		Map<String, DocumentedField> fieldMap = new LinkedHashMap<>();

		if (!EventJS.class.isAssignableFrom(c))
		{
			for (Field field : c.getFields())
			{
				int m = field.getModifiers();

				if (Modifier.isPublic(m) && !Modifier.isStatic(m) && !field.isAnnotationPresent(Ignore.class))
				{
					DocumentedField f = new DocumentedField(documentation, field);
					fieldMap.put(f.name, f);
				}
			}
		}

		List<DocumentedMethod> methodList = new ArrayList<>();

		for (Method method : c.getMethods())
		{
			int mo = method.getModifiers();

			if (Modifier.isPublic(mo) && !Modifier.isStatic(mo) && !method.isAnnotationPresent(Ignore.class))
			{
				if (!Documentation.OBJECT_METHODS.contains(method.getName()))
				{
					methodList.add(new DocumentedMethod(documentation, method));
				}
			}
		}

		Map<String, MethodBean> beanMap = new LinkedHashMap<>();

		for (DocumentedMethod m : methodList)
		{
			if (m.beanType != -1 && !m.beanName.isEmpty())
			{
				MethodBean bean = beanMap.get(m.beanName);

				if (bean == null)
				{
					bean = new MethodBean(m.beanName);
					beanMap.put(m.beanName, bean);
				}

				bean.methods[m.beanType] = m;
			}
		}

		for (MethodBean bean : beanMap.values())
		{
			DocumentedField field = new DocumentedField(documentation, bean);

			if (field.type != null && field.actualType != null)
			{
				fieldMap.put(field.name, field);
			}
		}

		if (!fieldMap.isEmpty())
		{
			List<DocumentedField> fieldList = new ArrayList<>(fieldMap.values());
			fieldList.sort(null);

			Tag methodTable = body.table().addClass("doc").id("fields");
			Tag firstRow = methodTable.tr();
			firstRow.th().a("Fields", "#fields");
			firstRow.th().text("Getter");
			firstRow.th().text("Setter");

			for (DocumentedField field : fieldList)
			{
				Tag row = methodTable.tr();
				Tag n = row.td().span("", "");
				KubeJSHomePage.classText(documentation, n, field.type, field.actualType);
				n.text(" ");
				n.text(field.name);

				if (!field.info.isEmpty())
				{
					n.text(" ");
					KubeJSHomePage.emoji(n, "x1F4A1", field.info);
				}

				KubeJSHomePage.yesNoSpan(row.td(), field.getter);
				KubeJSHomePage.yesNoSpan(row.td(), field.setter);
			}

			body.br();
		}

		if (!methodList.isEmpty())
		{
			methodList.sort(null);

			Tag methodTable = body.table().addClass("doc").id("methods");
			methodTable.tr().th().a("Methods", "#methods");

			for (DocumentedMethod method : methodList)
			{
				if (method.beanType != -1 && !method.beanName.isEmpty())
				{
					continue;
				}

				Tag n = methodTable.tr().td().id(method.id);
				KubeJSHomePage.classText(documentation, n, method.returnType, method.actualReturnType);
				n.text(" ");
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

				if (!method.info.isEmpty())
				{
					n.text(" ");
					KubeJSHomePage.emoji(n, "x1F4A1", method.info);
				}
			}
		}

		body.br();
		body.p().paired("i", "Hosted from '" + FMLCommonHandler.instance().getMinecraftServerInstance().getMOTD() + "'");
		body.p().paired("i", "Mod version: " + ScriptModData.getInstance().getModVersion());
		body.p().paired("i", "Mod loader: " + ScriptModData.getInstance().getType());
		body.p().paired("i", "Minecraft version: " + ScriptModData.getInstance().getMcVersion());
		body.p().paired("i").a("Visit kubejs.latvian.dev for more info about the mod", "https://kubejs.latvian.dev");
	}
}
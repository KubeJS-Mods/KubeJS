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
import dev.latvian.mods.aurora.tag.PairedTag;
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
	public boolean addBackButton()
	{
		return false;
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
			topRow.th().a("Event", "#event").tooltip("ID of this event. For double events, alternate ID will be provided");
			topRow.th().text("Can cancel").tooltip("True if event can be cancelled");
			topRow.th().text("Client").tooltip("True if event is fired on client side");
			topRow.th().text("Server").tooltip("True if event is fired on server side");
			Tag row = table.tr();
			row.td().text(event.eventID);
			row.td().yesNoSpan(event.canCancel);
			row.td().yesNoSpan(event.sideOnly == null || event.sideOnly == Side.CLIENT);
			row.td().yesNoSpan(event.sideOnly == null || event.sideOnly == Side.SERVER);
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
				if (!Documentation.isObjectMethod(method.getName(), method.getParameterCount()))
				{
					methodList.add(new DocumentedMethod(documentation, method));
				}
			}
		}

		Map<MethodBeanName, MethodBean> beanMap = new LinkedHashMap<>();

		for (DocumentedMethod m : methodList)
		{
			if (m.bean != null && m.bean.type.isValid(m.paramNames.length))
			{
				MethodBean bean = beanMap.get(m.bean);

				if (bean == null)
				{
					bean = new MethodBean(m.bean);
					beanMap.put(m.bean, bean);
				}

				bean.methods.put(m.bean.type, m);
			}
		}

		for (MethodBean bean : beanMap.values())
		{
			if (!bean.methods.containsKey(MethodBeanName.Type.GET) && !bean.methods.containsKey(MethodBeanName.Type.IS))
			{
				continue;
			}

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
			firstRow.th().text("Type");

			for (DocumentedField field : fieldList)
			{
				Tag row = methodTable.tr();
				Tag n = row.td();

				n.icon(field.canSet ? "mutable" : "immutable").title(field.canSet ? "Field can be modified" : "Field can't be modified");
				n.text(" ");

				if (field.type != null)
				{
					Tag typeIcon = n.icon(KubeJSHomePage.getIconType(field.type));
					typeIcon.title(documentation.getPrettyName(field.type));
					n.text(" ");
				}

				//KubeJSHomePage.classText(documentation, n, field.type, field.actualType);
				//n.text(" ");
				n.text(field.name);

				if (!field.info.isEmpty())
				{
					n.text(" ");
					n.icon("info").tooltip(field.info);
				}

				KubeJSHomePage.classText(documentation, row.td(), field.type, field.actualType);
			}

			body.br();
		}

		if (!methodList.isEmpty())
		{
			methodList.sort(null);

			Tag methodTable = body.table().addClass("doc").id("methods");
			Tag firstRow = methodTable.tr();
			firstRow.th().a("Methods", "#methods");
			firstRow.th().text("Return Type");

			boolean isEvent = EventJS.class.isAssignableFrom(c);

			for (DocumentedMethod method : methodList)
			{
				if (isEvent && method.paramNames.length == 0)
				{
					if (method.name.equals("canCancel"))
					{
						continue;
					}
					else if ((event == null || !event.canCancel) && method.name.equals("cancel"))
					{
						continue;
					}
				}

				MethodBean bean = method.bean != null ? beanMap.get(method.bean) : null;

				if (bean != null && (bean.methods.containsKey(MethodBeanName.Type.GET) || bean.methods.containsKey(MethodBeanName.Type.IS)))
				{
					continue;
				}

				Tag row = methodTable.tr();
				Tag n = row.td().id(method.id);

				Tag classSpan = new PairedTag("span");

				if (method.returnType != null)
				{
					Tag typeIcon = n.icon(KubeJSHomePage.getIconType(method.returnType));
					typeIcon.title(documentation.getPrettyName(method.returnType));
					n.text(" ");
				}

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
					n.icon("info").tooltip(method.info);
				}

				KubeJSHomePage.classText(documentation, row.td(), method.returnType, method.actualReturnType);
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
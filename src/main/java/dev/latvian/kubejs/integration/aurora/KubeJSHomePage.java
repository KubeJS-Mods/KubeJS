package dev.latvian.kubejs.integration.aurora;

import dev.latvian.kubejs.documentation.Documentation;
import dev.latvian.kubejs.documentation.DocumentedBinding;
import dev.latvian.kubejs.documentation.DocumentedEvent;
import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.kubejs.script.ScriptModData;
import dev.latvian.mods.aurora.page.HTTPWebPage;
import dev.latvian.mods.aurora.tag.Tag;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class KubeJSHomePage extends HTTPWebPage
{
	private final Documentation documentation;

	public KubeJSHomePage(Documentation d)
	{
		documentation = d;
	}

	@Override
	public void head(Tag head)
	{
		head.paired("title", "KubeJS Documentation");
		head.unpaired("link").attr("rel", "stylesheet").attr("type", "text/css").attr("href", "https://kubejs.latvian.dev/style.css");
		head.unpaired("link").attr("rel", "icon").attr("href", "https://kubejs.latvian.dev/logo_48.png");
	}

	@Override
	public void body(Tag body)
	{
		body.img("https://kubejs.latvian.dev/logo_title.png").style("height", "7em");
		body.br();
		body.h1("").a("KubeJS Documentation", "/kubejs");

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
			classText(documentation, row.td(), object.type);
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
			classText(documentation, row.td(), object.type);
			row.td().text(object.value);
		}

		body.h2("Events").id("events");

		List<DocumentedEvent> list = new ArrayList<>(documentation.events.values());
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
			classText(documentation, row.td(), event.eventClass);
			yesNoSpan(row.td(), event.canCancel);
			yesNoSpan(row.td(), event.sideOnly == null || event.sideOnly == Side.CLIENT);
			yesNoSpan(row.td(), event.sideOnly == null || event.sideOnly == Side.SERVER);
		}

		body.br();
		body.p().paired("i", "Hosted from '" + FMLCommonHandler.instance().getMinecraftServerInstance().getMOTD() + "'");
		body.p().paired("i", "Mod version: " + ScriptModData.getInstance().getModVersion());
		body.p().paired("i", "Mod loader: " + ScriptModData.getInstance().getType());
		body.p().paired("i", "Minecraft version: " + ScriptModData.getInstance().getMcVersion());
		body.p().paired("i").a("Visit kubejs.latvian.dev for more info about the mod", "https://kubejs.latvian.dev");
	}

	public static void classText(Documentation d, Tag parent, Class c, Type t)
	{
		Class ac = d.getActualType(c);

		if (ac.isPrimitive() || ac == Character.class || Number.class.isAssignableFrom(ac))
		{
			parent.span(d.getPrettyName(c), "type");
			return;
		}

		Tag tag = parent.span("", "");
		tag.a(d.getPrettyName(c), "/kubejs/" + ac.getName()).addClass("type").title("More Info");

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
					classText(d, tag, (Class) at[i], at[i]);
				}
				else
				{
					tag.span(at[i].getTypeName(), "type");
				}
			}

			tag.text(">");
		}
	}

	public static void classText(Documentation d, Tag parent, Class c)
	{
		classText(d, parent, c, c);
	}

	public static void yesNoSpan(Tag parent, boolean value)
	{
		parent.span(value ? "Yes" : "No", value ? "yes" : "no");
	}
}
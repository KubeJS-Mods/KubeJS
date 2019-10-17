package dev.latvian.kubejs.integration.aurora;

import dev.latvian.kubejs.documentation.Documentation;
import dev.latvian.kubejs.documentation.DocumentedBinding;
import dev.latvian.kubejs.documentation.DocumentedEvent;
import dev.latvian.kubejs.fluid.FluidStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.kubejs.script.ScriptModData;
import dev.latvian.kubejs.util.nbt.NBTBaseJS;
import dev.latvian.mods.aurora.page.HTTPWebPage;
import dev.latvian.mods.aurora.tag.Tag;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
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
	public String getTitle()
	{
		return "KubeJS Documentation";
	}

	@Override
	public String getDescription()
	{
		return "Index";
	}

	@Override
	public String getIcon()
	{
		return "https://kubejs.latvian.dev/logo_48.png";
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
			Tag n = row.td();
			Tag typeIcon = n.icon(KubeJSHomePage.getIconType(object.type));
			typeIcon.title(documentation.getPrettyName(object.type));
			n.text(" ");
			n.text(object.name);
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
			Tag n = row.td();
			Tag typeIcon = n.icon(KubeJSHomePage.getIconType(object.type));
			typeIcon.title(documentation.getPrettyName(object.type));
			n.text(" ");
			n.text(object.name);
			classText(documentation, row.td(), object.type);
			row.td().text(object.value);
		}

		body.h2("Events").id("events");

		List<DocumentedEvent> list = new ArrayList<>(documentation.events.values());
		list.sort(null);

		Tag eventTable = body.paired("table", "").addClass("doc");
		Tag topRow = eventTable.tr();
		topRow.th().text("ID").tooltip("ID of this event. For double events, alternate ID will be provided");
		topRow.th().text("Type").tooltip("Class of this event");
		topRow.th().text("Can cancel").tooltip("True if event can be cancelled");
		topRow.th().text("Client").tooltip("True if event is fired on client side");
		topRow.th().text("Server").tooltip("True if event is fired on server side");

		for (DocumentedEvent event : list)
		{
			Tag row = eventTable.tr();
			row.td().text(event.eventID);
			classText(documentation, row.td(), event.eventClass);
			row.td().yesNoSpan(event.canCancel);
			row.td().yesNoSpan(event.sideOnly == null || event.sideOnly == Side.CLIENT);
			row.td().yesNoSpan(event.sideOnly == null || event.sideOnly == Side.SERVER);
		}

		body.br();
		body.p().paired("i", "Hosted from '" + FMLCommonHandler.instance().getMinecraftServerInstance().getMOTD() + "'");
		body.p().paired("i", "Mod version: " + ScriptModData.getInstance().getModVersion());
		body.p().paired("i", "Mod loader: " + ScriptModData.getInstance().getType());
		body.p().paired("i", "Minecraft version: " + ScriptModData.getInstance().getMcVersion());
		body.p().paired("i").a("Visit kubejs.latvian.dev for more info about the mod", "https://kubejs.latvian.dev");
	}

	public static String classText(Documentation d, Tag parent, @Nullable Class c, @Nullable Type t)
	{
		Class ac = d.getActualType(c);

		if (ac == null || t == null)
		{
			return "";
		}

		if (ac.isPrimitive() || ac == Character.class || Number.class.isAssignableFrom(ac))
		{
			parent.span(d.getPrettyName(c), "type");
			return "";
		}

		Tag tag = parent.span("", "");
		String url = "/kubejs/" + ac.getName();
		tag.a(d.getPrettyName(c), url).addClass("type");

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

		return url;
	}

	public static void classText(Documentation d, Tag parent, Class c)
	{
		classText(d, parent, c, c);
	}

	public static String getIconType(Class c)
	{
		if (c == void.class || c == Void.class)
		{
			return "void";
		}
		else if (c == char.class || c == Character.class || CharSequence.class.isAssignableFrom(c))
		{
			return "text";
		}
		else if (c == boolean.class || c == Boolean.class)
		{
			return "boolean";
		}
		else if (c == byte.class || c == Byte.class)
		{
			return "byte";
		}
		else if (c == short.class || c == Short.class)
		{
			return "short";
		}
		else if (c == int.class || c == Integer.class)
		{
			return "int";
		}
		else if (c == long.class || c == Long.class)
		{
			return "long";
		}
		else if (c == float.class || c == Float.class)
		{
			return "float";
		}
		else if (c == double.class || c == Double.class)
		{
			return "double";
		}
		else if (ItemStackJS.class.isAssignableFrom(c))
		{
			return "item";
		}
		else if (FluidStackJS.class.isAssignableFrom(c))
		{
			return "fluid";
		}
		else if (IngredientJS.class.isAssignableFrom(c))
		{
			return "ingredient";
		}
		else if (NBTBaseJS.class.isAssignableFrom(c))
		{
			return "nbt";
		}

		return "object";
	}
}
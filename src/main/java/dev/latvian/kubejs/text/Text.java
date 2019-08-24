package dev.latvian.kubejs.text;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author LatvianModder
 */
public abstract class Text implements Iterable<Text>
{
	private TextColor color;
	private Boolean bold;
	private Boolean italic;
	private Boolean underlined;
	private Boolean strikethrough;
	private Boolean obfuscated;
	private String insertion;
	private String click;
	private Text hover;
	private List<Text> siblings;

	public abstract ITextComponent rawComponent();

	public abstract Text rawCopy();

	public abstract JsonElement toJson();

	public final ITextComponent component()
	{
		ITextComponent component = rawComponent();

		if (color != null)
		{
			component.getStyle().setColor(color.textFormatting);
		}

		component.getStyle().setBold(bold);
		component.getStyle().setItalic(italic);
		component.getStyle().setUnderlined(underlined);
		component.getStyle().setStrikethrough(strikethrough);
		component.getStyle().setObfuscated(obfuscated);
		component.getStyle().setInsertion(insertion);

		if (click != null)
		{
			component.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, click));
		}

		if (hover != null)
		{
			component.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover.component()));
		}

		for (Text text : siblings())
		{
			component.appendSibling(text.component());
		}

		return component;
	}

	public final Text copy()
	{
		Text t = rawCopy();
		t.color = color;
		t.bold = bold;
		t.italic = italic;
		t.underlined = underlined;
		t.strikethrough = strikethrough;
		t.obfuscated = obfuscated;
		t.insertion = insertion;
		t.click = click;
		t.hover = hover == null ? null : hover.copy();

		for (Text child : siblings())
		{
			t.append(child.copy());
		}

		return t;
	}

	public final JsonObject getPropertiesAsJson()
	{
		JsonObject json = new JsonObject();

		if (color != null)
		{
			json.addProperty("color", color.textFormatting.getFriendlyName());
		}

		if (bold != null)
		{
			json.addProperty("bold", bold);
		}

		if (italic != null)
		{
			json.addProperty("italic", italic);
		}

		if (underlined != null)
		{
			json.addProperty("underlined", underlined);
		}

		if (strikethrough != null)
		{
			json.addProperty("strikethrough", strikethrough);
		}

		if (obfuscated != null)
		{
			json.addProperty("obfuscated", obfuscated);
		}

		if (insertion != null)
		{
			json.addProperty("insertion", insertion);
		}

		if (click != null)
		{
			json.addProperty("click", click);
		}

		if (hover != null)
		{
			json.add("hover", hover.toJson());
		}

		if (!siblings().isEmpty())
		{
			JsonArray array = new JsonArray();

			for (Text child : siblings())
			{
				array.add(child.toJson());
			}

			json.add("extra", array);
		}

		return json;
	}

	public final void setPropertiesFromJson(JsonObject json)
	{
		if (json.has("color"))
		{
			color = TextColor.MAP.get(json.get("color").getAsString());
		}

		bold = json.has("bold") ? json.get("bold").getAsBoolean() : null;
		italic = json.has("italic") ? json.get("italic").getAsBoolean() : null;
		underlined = json.has("underlined") ? json.get("underlined").getAsBoolean() : null;
		strikethrough = json.has("strikethrough") ? json.get("strikethrough").getAsBoolean() : null;
		obfuscated = json.has("obfuscated") ? json.get("obfuscated").getAsBoolean() : null;
		insertion = json.has("insertion") ? json.get("insertion").getAsString() : null;
		click = json.has("click") ? json.get("click").getAsString() : null;
		hover = json.has("hover") ? TextUtilsJS.INSTANCE.fromJson(json.get("hover")) : null;

		siblings = null;

		if (json.has("extra"))
		{
			for (JsonElement e : json.get("extra").getAsJsonArray())
			{
				append(TextUtilsJS.INSTANCE.fromJson(e));
			}
		}
	}

	@Override
	public final Iterator<Text> iterator()
	{
		if (siblings().isEmpty())
		{
			return Collections.singleton(this).iterator();
		}

		List<Text> list = new ArrayList<>();
		list.add(this);

		for (Text child : siblings())
		{
			for (Text part : child)
			{
				list.add(part);
			}
		}

		return list.iterator();
	}

	public final Text color(TextColor value)
	{
		color = value;
		return this;
	}

	public final Text bold(Boolean value)
	{
		bold = value;
		return this;
	}

	public final Text bold()
	{
		return bold(true);
	}

	public final Text italic(Boolean value)
	{
		italic = value;
		return this;
	}

	public final Text italic()
	{
		return italic(true);
	}

	public final Text underlined(Boolean value)
	{
		underlined = value;
		return this;
	}

	public final Text underlined()
	{
		return underlined(true);
	}

	public final Text strikethrough(Boolean value)
	{
		strikethrough = value;
		return this;
	}

	public final Text strikethrough()
	{
		return strikethrough(true);
	}

	public final Text obfuscated(Boolean value)
	{
		obfuscated = value;
		return this;
	}

	public final Text obfuscated()
	{
		return obfuscated(true);
	}

	public final Text insertion(String value)
	{
		insertion = value;
		return this;
	}

	public final Text click(String value)
	{
		click = value;
		return this;
	}

	public final Text hover(Object text)
	{
		hover = TextUtilsJS.INSTANCE.of(text);
		return this;
	}

	public final void append(Text sibling)
	{
		if (siblings == null)
		{
			siblings = new LinkedList<>();
		}

		siblings.add(sibling);
	}

	public final List<Text> siblings()
	{
		return siblings == null ? Collections.emptyList() : siblings;
	}
}
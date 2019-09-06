package dev.latvian.kubejs.text;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.documentation.DocClass;
import dev.latvian.kubejs.documentation.DocMethod;
import dev.latvian.kubejs.documentation.Param;
import dev.latvian.kubejs.util.JsonSerializable;
import dev.latvian.kubejs.util.JsonUtilsJS;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author LatvianModder
 */
@DocClass
public abstract class Text implements Iterable<Text>, Comparable<Text>, JsonSerializable
{
	public static Text of(@Nullable Object o)
	{
		if (o == null)
		{
			return new TextString("null");
		}
		else if (o instanceof CharSequence)
		{
			return new TextString(o.toString());
		}
		else if (o instanceof Text)
		{
			return (Text) o;
		}
		else if (o instanceof JsonElement)
		{
			return fromJson((JsonElement) o);
		}
		else if (o instanceof ITextComponent)
		{
			Text t = new TextString("");

			for (ITextComponent c : ((ITextComponent) o))
			{
				Text t1;

				if (c instanceof TextComponentTranslation)
				{
					t1 = new TextTranslate(((TextComponentTranslation) c).getKey(), ((TextComponentTranslation) c).getFormatArgs());
				}
				else
				{
					t1 = new TextString(c.getUnformattedComponentText());
				}

				t1.bold(c.getStyle().getBold());
				t1.italic(c.getStyle().getItalic());
				t1.underlined(c.getStyle().getUnderlined());
				t1.strikethrough(c.getStyle().getStrikethrough());
				t1.obfuscated(c.getStyle().getObfuscated());
				t1.insertion(c.getStyle().getInsertion());

				ClickEvent ce = c.getStyle().getClickEvent();

				if (ce != null)
				{
					if (ce.getAction() == ClickEvent.Action.RUN_COMMAND)
					{
						t1.click("command:" + ce.getValue());
					}
					else if (ce.getAction() == ClickEvent.Action.SUGGEST_COMMAND)
					{
						t1.click("suggest_command:" + ce.getValue());
					}
					else if (ce.getAction() == ClickEvent.Action.OPEN_URL)
					{
						t1.click(ce.getValue());
					}
				}

				HoverEvent he = c.getStyle().getHoverEvent();

				if (he != null && he.getAction() == HoverEvent.Action.SHOW_TEXT)
				{
					t1.hover(of(he.getValue()));
				}

				t.append(t1);
			}

			return t;
		}

		return fromJson(JsonUtilsJS.of(o));
	}

	public static Text fromJson(JsonElement e)
	{
		if (e.isJsonNull())
		{
			return new TextString("null");
		}
		else if (e.isJsonArray())
		{
			Text text = new TextString("");

			for (JsonElement e1 : e.getAsJsonArray())
			{
				text.append(fromJson(e1));
			}

			return text;
		}
		else if (e.isJsonObject())
		{
			JsonObject o = e.getAsJsonObject();

			if (o.has("text") || o.has("translate"))
			{
				Text text;

				if (o.has("text"))
				{
					text = new TextString(o.get("text").getAsString());
				}
				else
				{
					Object[] with;

					if (o.has("with"))
					{
						JsonArray a = o.get("with").getAsJsonArray();
						with = new Object[a.size()];
						int i = 0;

						for (JsonElement e1 : a)
						{
							with[i] = JsonUtilsJS.primitiveObject(e1);

							if (with[i] == null)
							{
								with[i] = fromJson(e1);
							}

							i++;
						}
					}
					else
					{
						with = new Object[0];
					}

					text = new TextTranslate(o.get("translate").getAsString(), with);
				}

				text.setPropertiesFromJson(o);
				return text;
			}
		}

		return new TextString(e.getAsString());
	}

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

	@Override
	@DocMethod(value = "Convert text to json")
	public abstract JsonElement json();

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
			if (click.startsWith("command:"))
			{
				component.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, click.substring(8)));
			}
			else if (click.startsWith("suggest_command:"))
			{
				component.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, click.substring(16)));
			}
			else
			{
				component.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, click));
			}
		}

		if (hover != null)
		{
			component.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover.component()));
		}

		for (Text text : getSiblings())
		{
			component.appendSibling(text.component());
		}

		return component;
	}

	public final String getUnformattedString()
	{
		return component().getUnformattedText();
	}

	public final String getFormattedString()
	{
		return component().getFormattedText();
	}

	@DocMethod(value = "Create a deep copy of this text")
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

		for (Text child : getSiblings())
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
			json.add("hover", hover.json());
		}

		if (!getSiblings().isEmpty())
		{
			JsonArray array = new JsonArray();

			for (Text child : getSiblings())
			{
				array.add(child.json());
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
		hover = json.has("hover") ? Text.fromJson(json.get("hover")) : null;

		siblings = null;

		if (json.has("extra"))
		{
			for (JsonElement e : json.get("extra").getAsJsonArray())
			{
				append(Text.fromJson(e));
			}
		}
	}

	@Override
	public final Iterator<Text> iterator()
	{
		if (getSiblings().isEmpty())
		{
			return Collections.singleton(this).iterator();
		}

		List<Text> list = new ArrayList<>();
		list.add(this);

		for (Text child : getSiblings())
		{
			for (Text part : child)
			{
				list.add(part);
			}
		}

		return list.iterator();
	}

	@DocMethod(value = "Set color", params = @Param(value = "value"))
	public final Text color(TextColor value)
	{
		color = value;
		return this;
	}

	@DocMethod(value = "Set color to black")
	public final Text black()
	{
		return color(TextColor.BLACK);
	}

	@DocMethod(value = "Set color to dark blue")
	public final Text darkBlue()
	{
		return color(TextColor.DARK_BLUE);
	}

	@DocMethod(value = "Set color to dark green")
	public final Text darkGreen()
	{
		return color(TextColor.DARK_GREEN);
	}

	@DocMethod(value = "Set color to dark aqua")
	public final Text darkAqua()
	{
		return color(TextColor.DARK_AQUA);
	}

	@DocMethod(value = "Set color to dark red")
	public final Text darkRed()
	{
		return color(TextColor.DARK_RED);
	}

	@DocMethod(value = "Set color to dark purple")
	public final Text darkPurple()
	{
		return color(TextColor.DARK_PURPLE);
	}

	@DocMethod(value = "Set color to gold")
	public final Text gold()
	{
		return color(TextColor.GOLD);
	}

	@DocMethod(value = "Set color to gray")
	public final Text gray()
	{
		return color(TextColor.GRAY);
	}

	@DocMethod(value = "Set color to dark gray")
	public final Text darkGray()
	{
		return color(TextColor.DARK_GRAY);
	}

	@DocMethod(value = "Set color to blue")
	public final Text blue()
	{
		return color(TextColor.BLUE);
	}

	@DocMethod(value = "Set color to green")
	public final Text green()
	{
		return color(TextColor.GREEN);
	}

	@DocMethod(value = "Set color to aqua")
	public final Text aqua()
	{
		return color(TextColor.AQUA);
	}

	@DocMethod(value = "Set color to red")
	public final Text red()
	{
		return color(TextColor.RED);
	}

	@DocMethod(value = "Set color to light purple")
	public final Text lightPurple()
	{
		return color(TextColor.LIGHT_PURPLE);
	}

	@DocMethod(value = "Set color to yellow")
	public final Text yellow()
	{
		return color(TextColor.YELLOW);
	}

	@DocMethod(value = "Set color to white")
	public final Text white()
	{
		return color(TextColor.WHITE);
	}

	@DocMethod(value = "Set bold")
	public final Text bold(@Nullable Boolean value)
	{
		bold = value;
		return this;
	}

	@DocMethod(value = "Set bold")
	public final Text bold()
	{
		return bold(true);
	}

	@DocMethod(value = "Set italic")
	public final Text italic(@Nullable Boolean value)
	{
		italic = value;
		return this;
	}

	@DocMethod(value = "Set italic")
	public final Text italic()
	{
		return italic(true);
	}

	@DocMethod(value = "Set underlined")
	public final Text underlined(@Nullable Boolean value)
	{
		underlined = value;
		return this;
	}

	@DocMethod(value = "Set underlined")
	public final Text underlined()
	{
		return underlined(true);
	}

	@DocMethod(value = "Set strikethrough")
	public final Text strikethrough(@Nullable Boolean value)
	{
		strikethrough = value;
		return this;
	}

	@DocMethod(value = "Set strikethrough")
	public final Text strikethrough()
	{
		return strikethrough(true);
	}

	@DocMethod(value = "Set obfuscated")
	public final Text obfuscated(@Nullable Boolean value)
	{
		obfuscated = value;
		return this;
	}

	@DocMethod(value = "Set obfuscated")
	public final Text obfuscated()
	{
		return obfuscated(true);
	}

	@DocMethod(value = "Set insertion text")
	public final Text insertion(@Nullable String value)
	{
		insertion = value;
		return this;
	}

	@DocMethod(value = "Set click URL")
	public final Text click(@Nullable String value)
	{
		click = value;
		return this;
	}

	@DocMethod(value = "Set hover text", params = @Param(type = Text.class))
	public final Text hover(@Nullable Object text)
	{
		hover = of(text);
		return this;
	}

	@DocMethod(value = "Append text and end of this one", params = @Param(type = Text.class))
	public final Text append(Object sibling)
	{
		if (siblings == null)
		{
			siblings = new LinkedList<>();
		}

		siblings.add(of(sibling));
		return this;
	}

	@DocMethod(value = "List of siblings")
	public final List<Text> getSiblings()
	{
		return siblings == null ? Collections.emptyList() : siblings;
	}

	@DocMethod(value = "True if this text component has sibling components")
	public final boolean hasSiblings()
	{
		return siblings != null && !siblings.isEmpty();
	}

	@Override
	public String toString()
	{
		return component().getUnformattedText();
	}

	@Override
	public int compareTo(Text o)
	{
		return toString().compareTo(toString());
	}
}
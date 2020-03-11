package dev.latvian.kubejs.text;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.util.JSObjectType;
import dev.latvian.kubejs.util.JsonSerializable;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.util.WrappedJS;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author LatvianModder
 */
public abstract class Text implements Iterable<Text>, Comparable<Text>, JsonSerializable, WrappedJS
{
	public static Text of(@Nullable Object o)
	{
		return ofWrapped(UtilsJS.wrap(o, JSObjectType.ANY));
	}

	private static Text ofWrapped(@Nullable Object o)
	{
		if (o == null)
		{
			return new TextString("null");
		}
		else if (o instanceof CharSequence || o instanceof Number || o instanceof Character)
		{
			return new TextString(o.toString());
		}
		else if (o instanceof Enum)
		{
			return new TextString(((Enum) o).name());
		}
		else if (o instanceof Text)
		{
			return (Text) o;
		}
		else if (o instanceof ListJS)
		{
			Text text = new TextString("");

			for (Object e1 : (ListJS) o)
			{
				text.append(ofWrapped(e1));
			}

			return text;
		}
		else if (o instanceof MapJS)
		{
			MapJS map = (MapJS) o;

			if (map.containsKey("text") || map.containsKey("translate"))
			{
				Text text;

				if (map.containsKey("text"))
				{
					text = new TextString(map.get("text").toString());
				}
				else
				{
					Object[] with;

					if (map.containsKey("with"))
					{
						ListJS a = map.getOrNewList("with");
						with = new Object[a.size()];
						int i = 0;

						for (Object e1 : a)
						{
							with[i] = e1;

							if (with[i] instanceof MapJS || with[i] instanceof ListJS)
							{
								with[i] = ofWrapped(e1);
							}

							i++;
						}
					}
					else
					{
						with = new Object[0];
					}

					text = new TextTranslate(map.get("translate").toString(), with);
				}

				if (map.containsKey("color"))
				{
					text.color = TextColor.MAP.get(map.get("color").toString());
				}

				text.bold = map.containsKey("bold") ? (Boolean) map.get("bold") : null;
				text.italic = map.containsKey("italic") ? (Boolean) map.get("italic") : null;
				text.underlined = map.containsKey("underlined") ? (Boolean) map.get("underlined") : null;
				text.strikethrough = map.containsKey("strikethrough") ? (Boolean) map.get("strikethrough") : null;
				text.obfuscated = map.containsKey("obfuscated") ? (Boolean) map.get("obfuscated") : null;
				text.insertion = map.containsKey("insertion") ? map.get("insertion").toString() : null;
				text.click = map.containsKey("click") ? map.get("click").toString() : null;
				text.hover = map.containsKey("hover") ? ofWrapped(map.get("hover")) : null;

				text.siblings = null;

				if (map.containsKey("extra"))
				{
					for (Object e : map.getOrNewList("extra"))
					{
						text.append(ofWrapped(e));
					}
				}
				return text;
			}
		}

		return new TextString(o.toString());
	}

	public static Text join(Text separator, Iterable<Text> texts)
	{
		Text text = new TextString("");
		boolean first = true;

		for (Text t : texts)
		{
			if (first)
			{
				first = false;
			}
			else
			{
				text.append(separator);
			}

			text.append(t);
		}

		return text;
	}

	public static Text read(PacketBuffer buffer)
	{
		return Text.of(buffer.readTextComponent());
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
			if (click.startsWith("command:"))
			{
				component.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, click.substring(8)));
			}
			else if (click.startsWith("suggest_command:"))
			{
				component.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, click.substring(16)));
			}
			else if (click.startsWith("copy:"))
			{
				component.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, click.substring(5)));
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
		return component().getString();
	}

	public final String getFormattedString()
	{
		return component().getFormattedText();
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
			json.add("hover", hover.toJson());
		}

		if (!getSiblings().isEmpty())
		{
			JsonArray array = new JsonArray();

			for (Text child : getSiblings())
			{
				array.add(child.toJson());
			}

			json.add("extra", array);
		}

		return json;
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

	public final Text color(TextColor value)
	{
		color = value;
		return this;
	}

	public final Text black()
	{
		return color(TextColor.BLACK);
	}

	public final Text darkBlue()
	{
		return color(TextColor.DARK_BLUE);
	}

	public final Text darkGreen()
	{
		return color(TextColor.DARK_GREEN);
	}

	public final Text darkAqua()
	{
		return color(TextColor.DARK_AQUA);
	}

	public final Text darkRed()
	{
		return color(TextColor.DARK_RED);
	}

	public final Text darkPurple()
	{
		return color(TextColor.DARK_PURPLE);
	}

	public final Text gold()
	{
		return color(TextColor.GOLD);
	}

	public final Text gray()
	{
		return color(TextColor.GRAY);
	}

	public final Text darkGray()
	{
		return color(TextColor.DARK_GRAY);
	}

	public final Text blue()
	{
		return color(TextColor.BLUE);
	}

	public final Text green()
	{
		return color(TextColor.GREEN);
	}

	public final Text aqua()
	{
		return color(TextColor.AQUA);
	}

	public final Text red()
	{
		return color(TextColor.RED);
	}

	public final Text lightPurple()
	{
		return color(TextColor.LIGHT_PURPLE);
	}

	public final Text yellow()
	{
		return color(TextColor.YELLOW);
	}

	public final Text white()
	{
		return color(TextColor.WHITE);
	}

	public final Text bold(@Nullable Boolean value)
	{
		bold = value;
		return this;
	}

	public final Text bold()
	{
		return bold(true);
	}

	public final Text italic(@Nullable Boolean value)
	{
		italic = value;
		return this;
	}

	public final Text italic()
	{
		return italic(true);
	}

	public final Text underlined(@Nullable Boolean value)
	{
		underlined = value;
		return this;
	}

	public final Text underlined()
	{
		return underlined(true);
	}

	public final Text strikethrough(@Nullable Boolean value)
	{
		strikethrough = value;
		return this;
	}

	public final Text strikethrough()
	{
		return strikethrough(true);
	}

	public final Text obfuscated(@Nullable Boolean value)
	{
		obfuscated = value;
		return this;
	}

	public final Text obfuscated()
	{
		return obfuscated(true);
	}

	public final Text insertion(@Nullable String value)
	{
		insertion = value;
		return this;
	}

	public final Text click(@Nullable String value)
	{
		click = value;
		return this;
	}

	public final Text hover(Object text)
	{
		hover = of(text);
		return this;
	}

	public final Text append(Object sibling)
	{
		if (siblings == null)
		{
			siblings = new LinkedList<>();
		}

		siblings.add(of(sibling));
		return this;
	}

	public final List<Text> getSiblings()
	{
		return siblings == null ? Collections.emptyList() : siblings;
	}

	public final boolean hasSiblings()
	{
		return siblings != null && !siblings.isEmpty();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		else if (obj instanceof Text)
		{
			Text t = (Text) obj;

			if (color == t.color && bold == t.bold && italic == t.italic && underlined == t.underlined && strikethrough == t.strikethrough && obfuscated == t.obfuscated)
			{
				return Objects.equals(insertion, t.insertion) && Objects.equals(click, t.click) && Objects.equals(hover, t.hover) && Objects.equals(siblings, t.siblings);
			}
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(color, bold, italic, underlined, strikethrough, obfuscated, insertion, click, hover, siblings);
	}

	@Override
	public String toString()
	{
		return component().getString();
	}

	@Override
	public int compareTo(Text o)
	{
		return toString().compareTo(toString());
	}

	public void write(PacketBuffer buffer)
	{
		buffer.writeTextComponent(component());
	}
}
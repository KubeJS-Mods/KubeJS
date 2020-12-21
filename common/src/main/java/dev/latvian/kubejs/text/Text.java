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
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

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
					TextColor c = TextColor.MAP.get(map.get("color").toString());

					if (c != null)
					{
						text.color = c.color;
					}
				}

				text.bold = (Boolean) map.getOrDefault("bold", null);
				text.italic = (Boolean) map.getOrDefault("italic", null);
				text.underlined = (Boolean) map.getOrDefault("underlined", null);
				text.strikethrough = (Boolean) map.getOrDefault("strikethrough", null);
				text.obfuscated = (Boolean) map.getOrDefault("obfuscated", null);
				text.insertion = (String) map.getOrDefault("insertion", null);
				text.font = map.containsKey("font") ? new ResourceLocation(map.get("font").toString()) : null;
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

	public static Text read(FriendlyByteBuf buffer)
	{
		return Text.of(buffer.readComponent());
	}

	private int color = -1;
	private Boolean bold;
	private Boolean italic;
	private Boolean underlined;
	private Boolean strikethrough;
	private Boolean obfuscated;
	private String insertion;
	private ResourceLocation font;
	private String click;
	private Text hover;
	private List<Text> siblings;

	public abstract MutableComponent rawComponent();

	public abstract Text rawCopy();

	@Override
	public abstract JsonElement toJson();

	public final Component component()
	{
		MutableComponent component = rawComponent();
		Style style = component.getStyle();

		if (color != -1)
		{
			style = style.withColor(net.minecraft.network.chat.TextColor.fromRgb(color));
		}

		style = style.withBold(bold);
		style = style.withItalic(italic);

		if (Objects.equals(underlined, true))
		{
			style = style.applyFormat(ChatFormatting.UNDERLINE);
		}

		if (Objects.equals(strikethrough, true))
		{
			style = style.applyFormat(ChatFormatting.STRIKETHROUGH);
		}

		if (Objects.equals(obfuscated, true))
		{
			style = style.applyFormat(ChatFormatting.OBFUSCATED);
		}

		style = style.withInsertion(insertion);
		style = style.withFont(font);

		if (click != null)
		{
			if (click.startsWith("command:"))
			{
				style = style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, click.substring(8)));
			}
			else if (click.startsWith("suggest_command:"))
			{
				style = style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, click.substring(16)));
			}
			else if (click.startsWith("copy:"))
			{
				style = style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, click.substring(5)));
			}
			else
			{
				style = style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, click));
			}
		}

		if (hover != null)
		{
			style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover.component()));
		}

		component.setStyle(style);

		for (Text text : getSiblings())
		{
			component.append(text.component());
		}

		return component;
	}

	public final String getString()
	{
		return component().getString();
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
		t.font = font;
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

		if (color != -1)
		{
			json.addProperty("color", String.format("#%06X", color));
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

		if (font != null)
		{
			json.addProperty("font", font.toString());
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
		color = value.color & 0xFFFFFF;
		return this;
	}

	public final Text color(String value)
	{
		TextColor col = TextColor.MAP.get(value);

		if (col != null)
		{
			color = col.color & 0xFFFFFF;
		}
		else if (value.startsWith("#"))
		{
			color = Integer.decode(value) & 0xFFFFFF;
		}

		return this;
	}

	public final Text color(int col)
	{
		color = col & 0xFFFFFF;
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

	public final Text noColor()
	{
		color = -1;
		return this;
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

	public final Text font(@Nullable String value)
	{
		font = value == null || value.isEmpty() ? null : new ResourceLocation(value);
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
				return Objects.equals(insertion, t.insertion) && Objects.equals(font, t.font) && Objects.equals(click, t.click) && Objects.equals(hover, t.hover) && Objects.equals(siblings, t.siblings);
			}
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(color, bold, italic, underlined, strikethrough, obfuscated, insertion, font, click, hover, siblings);
	}

	@Override
	public String toString()
	{
		return component().getString();
	}

	@Override
	public int compareTo(Text other)
	{
		return toString().compareTo(other.toString());
	}

	public void write(FriendlyByteBuf buffer)
	{
		buffer.writeComponent(component());
	}
}
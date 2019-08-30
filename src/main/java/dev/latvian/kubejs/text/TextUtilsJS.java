package dev.latvian.kubejs.text;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.documentation.DocClass;
import dev.latvian.kubejs.documentation.DocMethod;
import dev.latvian.kubejs.documentation.Param;
import dev.latvian.kubejs.util.JsonUtilsJS;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
@DocClass("Text utilities")
public enum TextUtilsJS
{
	INSTANCE;

	@DocMethod("Creates text component from any object")
	public Text of(@Nullable Object object)
	{
		if (object instanceof CharSequence)
		{
			return new TextString(object.toString());
		}
		else if (object instanceof Text)
		{
			return (Text) object;
		}
		else if (object instanceof ITextComponent)
		{
			Text t = new TextString("");

			for (ITextComponent c : ((ITextComponent) object))
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

		return fromJson(JsonUtilsJS.INSTANCE.from(object));
	}

	@DocMethod("Creates text component from JSON")
	public Text fromJson(JsonElement e)
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
							with[i] = JsonUtilsJS.INSTANCE.primitiveObject(e1);

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

	@DocMethod(value = "Creates text component from string", params = @Param(type = String.class))
	public Text string(@Nullable Object text)
	{
		return new TextString(text);
	}

	@DocMethod("Creates text component from language key")
	public Text translate(String key)
	{
		return new TextTranslate(key, new Object[0]);
	}

	@DocMethod("Creates text component from language key and extra objects")
	public Text translate(String key, Object... objects)
	{
		return new TextTranslate(key, objects);
	}

	@DocMethod(value = "Black text", params = @Param(type = Text.class))
	public Text black(@Nullable Object text)
	{
		return of(text).black();
	}

	@DocMethod(value = "Dark blue text", params = @Param(type = Text.class))
	public Text darkBlue(@Nullable Object text)
	{
		return of(text).darkBlue();
	}

	@DocMethod(value = "Dark green text", params = @Param(type = Text.class))
	public Text darkGreen(@Nullable Object text)
	{
		return of(text).darkGreen();
	}

	@DocMethod(value = "Dark aqua text", params = @Param(type = Text.class))
	public Text darkAqua(@Nullable Object text)
	{
		return of(text).darkAqua();
	}

	@DocMethod(value = "Dark red text", params = @Param(type = Text.class))
	public Text darkRed(@Nullable Object text)
	{
		return of(text).darkRed();
	}

	@DocMethod(value = "Dark purple text", params = @Param(type = Text.class))
	public Text darkPurple(@Nullable Object text)
	{
		return of(text).darkPurple();
	}

	@DocMethod(value = "Gold text", params = @Param(type = Text.class))
	public Text gold(@Nullable Object text)
	{
		return of(text).gold();
	}

	@DocMethod(value = "Gray text", params = @Param(type = Text.class))
	public Text gray(@Nullable Object text)
	{
		return of(text).gray();
	}

	@DocMethod(value = "Dark gray text", params = @Param(type = Text.class))
	public Text darkGray(@Nullable Object text)
	{
		return of(text).darkGray();
	}

	@DocMethod(value = "Blue text", params = @Param(type = Text.class))
	public Text blue(@Nullable Object text)
	{
		return of(text).blue();
	}

	@DocMethod(value = "Green text", params = @Param(type = Text.class))
	public Text green(@Nullable Object text)
	{
		return of(text).green();
	}

	@DocMethod(value = "Aqua text", params = @Param(type = Text.class))
	public Text aqua(@Nullable Object text)
	{
		return of(text).aqua();
	}

	@DocMethod(value = "Red text", params = @Param(type = Text.class))
	public Text red(@Nullable Object text)
	{
		return of(text).red();
	}

	@DocMethod(value = "Light purple text", params = @Param(type = Text.class))
	public Text lightPurple(@Nullable Object text)
	{
		return of(text).lightPurple();
	}

	@DocMethod(value = "Yellow text", params = @Param(type = Text.class))
	public Text yellow(@Nullable Object text)
	{
		return of(text).yellow();
	}

	@DocMethod(value = "White text", params = @Param(type = Text.class))
	public Text white(@Nullable Object text)
	{
		return of(text).white();
	}
}
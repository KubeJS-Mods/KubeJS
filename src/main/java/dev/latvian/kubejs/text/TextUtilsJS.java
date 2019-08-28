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
			Text t;
			ITextComponent c = (ITextComponent) object;

			if (c instanceof TextComponentTranslation)
			{
				t = new TextTranslate(((TextComponentTranslation) c).getKey(), ((TextComponentTranslation) c).getFormatArgs());
			}
			else
			{
				t = new TextString(c.getUnformattedText());
			}

			for (ITextComponent component : c.getSiblings())
			{
				t.append(of(component));
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
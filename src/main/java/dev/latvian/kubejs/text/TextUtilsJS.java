package dev.latvian.kubejs.text;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.util.JsonUtilsJS;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public enum TextUtilsJS
{
	INSTANCE;

	public Text of(Object object)
	{
		if (object instanceof CharSequence)
		{
			return string(object.toString());
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
				t = translate(((TextComponentTranslation) c).getKey(), ((TextComponentTranslation) c).getFormatArgs());
			}
			else
			{
				t = string(c.getUnformattedText());
			}

			for (ITextComponent component : c.getSiblings())
			{
				t.append(of(component));
			}

			return t;
		}

		return fromJson(JsonUtilsJS.INSTANCE.from(object));
	}

	public Text fromJson(JsonElement e)
	{
		if (e.isJsonNull())
		{
			return string("null");
		}
		else if (e.isJsonArray())
		{
			Text text = string("");

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
					text = string(o.get("text").getAsString());
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

					text = translate(o.get("translate").getAsString(), with);
				}

				text.setPropertiesFromJson(o);
				return text;
			}
		}

		return string(e.getAsString());
	}

	public Text string(@Nullable Object text)
	{
		return new TextString(text);
	}

	public Text translate(String key)
	{
		return new TextTranslate(key, new Object[0]);
	}

	public Text translate(String key, Object... objects)
	{
		return new TextTranslate(key, objects);
	}
}
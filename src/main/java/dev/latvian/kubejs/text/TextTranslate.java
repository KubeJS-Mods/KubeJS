package dev.latvian.kubejs.text;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.util.JsonUtilsJS;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * @author LatvianModder
 */
public class TextTranslate extends Text
{
	private static final Object[] NO_OBJECTS = { };

	private final String key;
	private final Object[] objects;

	public TextTranslate(String k, Object[] o)
	{
		key = k;
		objects = o;

		for (int i = 0; i < objects.length; i++)
		{
			if (objects[i] instanceof ITextComponent || !(objects[i] instanceof Text) && JsonUtilsJS.primitiveObject(JsonUtilsJS.of(objects[i])) == null)
			{
				objects[i] = Text.of(objects[i]);
			}
		}
	}

	public TextTranslate(String k)
	{
		key = k;
		objects = NO_OBJECTS;
	}

	@Override
	public ITextComponent rawComponent()
	{
		Object[] o = new Object[objects.length];

		for (int i = 0; i < objects.length; i++)
		{
			if (objects[i] instanceof Text)
			{
				o[i] = ((Text) objects[i]).component();
			}
			else if (objects[i] instanceof ITextComponent)
			{
				o[i] = ((ITextComponent) objects[i]).createCopy();
			}
			else
			{
				o[i] = objects[i];
			}
		}

		return new TextComponentTranslation(key, o);
	}

	@Override
	public Text rawCopy()
	{
		Object[] o = new Object[objects.length];

		for (int i = 0; i < objects.length; i++)
		{
			if (objects[i] instanceof Text)
			{
				o[i] = ((Text) objects[i]).copy();
			}
			else if (objects[i] instanceof ITextComponent)
			{
				o[i] = ((ITextComponent) objects[i]).createCopy();
			}
			else
			{
				o[i] = objects[i];
			}
		}

		return new TextTranslate(key, o);
	}

	@Override
	public JsonObject json()
	{
		JsonObject o = getPropertiesAsJson();
		o.addProperty("translate", key);

		if (objects.length > 0)
		{
			JsonArray array = new JsonArray();

			for (Object ob : objects)
			{
				array.add(JsonUtilsJS.of(ob));
			}

			o.add("with", array);
		}

		return o;
	}
}
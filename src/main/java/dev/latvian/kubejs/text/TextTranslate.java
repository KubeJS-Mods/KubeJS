package dev.latvian.kubejs.text;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * @author LatvianModder
 */
public class TextTranslate extends Text
{
	private final String key;
	private final Object[] objects;

	public TextTranslate(String k, Object[] o)
	{
		key = k;
		objects = o;
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
}
package com.latmod.mods.worldjs.text;

/**
 * @author LatvianModder
 */
public enum TextUtils
{
	INSTANCE;

	public Text of(Object... o)
	{
		Text[] t = new Text[o.length];

		for (int i = 0; i < o.length; i++)
		{
			if (o[i] instanceof Text)
			{
				t[i] = (Text) o[i];
			}
			else
			{
				t[i] = new TextString(String.valueOf(o[i]));
			}
		}

		return t.length == 0 ? new TextString("") : t.length == 1 ? t[0] : new TextCombined(t);
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
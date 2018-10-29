package com.latmod.mods.worldjs.text;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.util.Arrays;
import java.util.List;

/**
 * @author LatvianModder
 */
public class TextCombined extends Text
{
	private final List<Text> children;

	public TextCombined(Text[] t)
	{
		children = Arrays.asList(t);
	}

	@Override
	public ITextComponent rawComponent()
	{
		ITextComponent component = new TextComponentString("");

		for (Text text : children)
		{
			component.appendSibling(text.component());
		}

		return component;
	}

	@Override
	public Text rawCopy()
	{
		Text[] t = children.toArray(new Text[0]);

		for (int i = 0; i < t.length; i++)
		{
			t[i] = t[i].copy();
		}

		return new TextCombined(t);
	}
}
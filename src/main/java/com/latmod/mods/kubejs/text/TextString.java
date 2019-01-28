package com.latmod.mods.kubejs.text;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

/**
 * @author LatvianModder
 */
public class TextString extends Text
{
	private final String string;

	public TextString(String s)
	{
		string = s;
	}

	@Override
	public ITextComponent rawComponent()
	{
		return new TextComponentString(string);
	}

	@Override
	public Text rawCopy()
	{
		return new TextString(string);
	}
}
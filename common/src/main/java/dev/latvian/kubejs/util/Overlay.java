package dev.latvian.kubejs.util;

import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class Overlay
{
	public final String id;
	public final List<Text> text;
	public int color;
	public boolean alwaysOnTop;
	public ItemStackJS icon;

	public Overlay(String i)
	{
		id = i;
		text = new ArrayList<>(1);
		color = 0x101010;
		alwaysOnTop = false;
		icon = EmptyItemStackJS.INSTANCE;
	}

	public Overlay add(Object o)
	{
		text.add(Text.of(o));
		return this;
	}

	public Overlay alwaysOnTop()
	{
		alwaysOnTop = true;
		return this;
	}

	public Overlay color(int col)
	{
		color = col;
		return this;
	}

	public Overlay color(String col)
	{
		return color(Long.decode(col).intValue());
	}

	public Overlay icon(Object o)
	{
		icon = ItemStackJS.of(o);
		return this;
	}
}
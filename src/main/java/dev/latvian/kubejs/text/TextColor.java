package dev.latvian.kubejs.text;

import net.minecraft.util.text.TextFormatting;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public enum TextColor
{
	BLACK("black", '0', 0x000000, TextFormatting.BLACK),
	DARK_BLUE("dark_blue", '1', 0x0000AA, TextFormatting.DARK_BLUE),
	DARK_GREEN("dark_green", '2', 0x00AA00, TextFormatting.DARK_GREEN),
	DARK_AQUA("dark_aqua", '3', 0x00AAAA, TextFormatting.DARK_AQUA),
	DARK_RED("dark_red", '4', 0xAA0000, TextFormatting.DARK_RED),
	DARK_PURPLE("dark_purple", '5', 0xAA00AA, TextFormatting.DARK_PURPLE),
	GOLD("gold", '6', 0xFFAA00, TextFormatting.GOLD),
	GRAY("gray", '7', 0xAAAAAA, TextFormatting.GRAY),
	DARK_GRAY("dark_gray", '8', 0x555555, TextFormatting.DARK_GRAY),
	BLUE("blue", '9', 0x5555FF, TextFormatting.BLUE),
	GREEN("green", 'a', 0x55FF55, TextFormatting.GREEN),
	AQUA("aqua", 'b', 0x55FFFF, TextFormatting.AQUA),
	RED("red", 'c', 0xFF5555, TextFormatting.RED),
	LIGHT_PURPLE("light_purple", 'd', 0xFF55FF, TextFormatting.LIGHT_PURPLE),
	YELLOW("yellow", 'e', 0xFFFF55, TextFormatting.YELLOW),
	WHITE("white", 'f', 0xFFFFFF, TextFormatting.WHITE);

	public static final Map<String, TextColor> MAP = new HashMap<>();

	static
	{
		for (TextColor color : values())
		{
			MAP.put(color.name, color);
		}
	}

	public final String name;
	public final char code;
	public final int color;
	public final TextFormatting textFormatting;

	TextColor(String n, char c, int h, TextFormatting f)
	{
		name = n;
		code = c;
		color = 0xFF000000 | h;
		textFormatting = f;
	}
}
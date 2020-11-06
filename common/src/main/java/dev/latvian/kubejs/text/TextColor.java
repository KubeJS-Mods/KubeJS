package dev.latvian.kubejs.text;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.ChatFormatting;

/**
 * @author LatvianModder
 */
public enum TextColor
{
	BLACK("black", '0', 0x000000, ChatFormatting.BLACK),
	DARK_BLUE("dark_blue", '1', 0x0000AA, ChatFormatting.DARK_BLUE),
	DARK_GREEN("dark_green", '2', 0x00AA00, ChatFormatting.DARK_GREEN),
	DARK_AQUA("dark_aqua", '3', 0x00AAAA, ChatFormatting.DARK_AQUA),
	DARK_RED("dark_red", '4', 0xAA0000, ChatFormatting.DARK_RED),
	DARK_PURPLE("dark_purple", '5', 0xAA00AA, ChatFormatting.DARK_PURPLE),
	GOLD("gold", '6', 0xFFAA00, ChatFormatting.GOLD),
	GRAY("gray", '7', 0xAAAAAA, ChatFormatting.GRAY),
	DARK_GRAY("dark_gray", '8', 0x555555, ChatFormatting.DARK_GRAY),
	BLUE("blue", '9', 0x5555FF, ChatFormatting.BLUE),
	GREEN("green", 'a', 0x55FF55, ChatFormatting.GREEN),
	AQUA("aqua", 'b', 0x55FFFF, ChatFormatting.AQUA),
	RED("red", 'c', 0xFF5555, ChatFormatting.RED),
	LIGHT_PURPLE("light_purple", 'd', 0xFF55FF, ChatFormatting.LIGHT_PURPLE),
	YELLOW("yellow", 'e', 0xFFFF55, ChatFormatting.YELLOW),
	WHITE("white", 'f', 0xFFFFFF, ChatFormatting.WHITE);

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
	public final ChatFormatting textFormatting;

	TextColor(String n, char c, int h, ChatFormatting f)
	{
		name = n;
		code = c;
		color = 0xFF000000 | h;
		textFormatting = f;
	}
}
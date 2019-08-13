package dev.latvian.kubejs.text;

import net.minecraft.util.text.TextFormatting;

/**
 * @author LatvianModder
 */
public enum TextColor
{
	BLACK('0', 0x000000, TextFormatting.BLACK),
	DARK_BLUE('1', 0x0000AA, TextFormatting.DARK_BLUE),
	DARK_GREEN('2', 0x00AA00, TextFormatting.DARK_GREEN),
	DARK_AQUA('3', 0x00AAAA, TextFormatting.DARK_AQUA),
	DARK_RED('4', 0xAA0000, TextFormatting.DARK_RED),
	DARK_PURPLE('5', 0xAA00AA, TextFormatting.DARK_PURPLE),
	GOLD('6', 0xFFAA00, TextFormatting.GOLD),
	GRAY('7', 0xAAAAAA, TextFormatting.GRAY),
	DARK_GRAY('8', 0x555555, TextFormatting.DARK_GRAY),
	BLUE('9', 0x5555FF, TextFormatting.BLUE),
	GREEN('a', 0x55FF55, TextFormatting.GREEN),
	AQUA('b', 0x55FFFF, TextFormatting.AQUA),
	RED('c', 0xFF5555, TextFormatting.RED),
	LIGHT_PURPLE('d', 0xFF55FF, TextFormatting.LIGHT_PURPLE),
	YELLOW('e', 0xFFFF55, TextFormatting.YELLOW),
	WHITE('f', 0xFFFFFF, TextFormatting.WHITE);

	public static final TextColor[] VALUES = values();

	public final char code;
	public final int color;
	public final TextFormatting _textFormatting;

	TextColor(char c, int h, TextFormatting f)
	{
		code = c;
		color = 0xFF000000 | h;
		_textFormatting = f;
	}
}
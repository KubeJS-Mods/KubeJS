package dev.latvian.mods.kubejs.bindings;

import dev.latvian.mods.kubejs.color.Color;
import dev.latvian.mods.kubejs.color.NoColor;
import dev.latvian.mods.kubejs.color.SimpleColor;
import dev.latvian.mods.kubejs.color.SimpleColorWithAlpha;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.world.item.DyeColor;

import java.util.HashMap;
import java.util.Map;

public interface ColorWrapper {
	Map<String, Color> MAP = new HashMap<>();
	Map<String, ChatFormatting> TEXT = Util.make(new HashMap<>(), map -> {
		for (ChatFormatting c : ChatFormatting.values()) {
			map.put(c.getName(), c);
		}
	});
	Map<String, DyeColor> DYE = Util.make(new HashMap<>(), map -> {
		for (DyeColor c : DyeColor.values()) {
			map.put(c.getName(), c);
		}
	});

	static Color of(Object o) {
		if (o instanceof Color) {
			return (Color) o;
		} else if (o instanceof String) {
			String s = o.toString();
			Color c = MAP.get(s);

			if (c != null) {
				return c;
			}

			if (s.startsWith("#")) {
				int col = Long.decode(s).intValue();
				return s.length() == 7 ? new SimpleColor(col) : new SimpleColorWithAlpha(col);
			}

			return NONE;
		} else if (o instanceof Number) {
			int i = ((Number) o).intValue();

			if (i == 0) {
				return NONE;
			} else {
				return new SimpleColor(i);
			}
		}

		return NONE;
	}

	static Color createMapped(Object o, String... names) {
		Color c = of(o);

		for (String s : names) {
			MAP.put(s, c);
		}

		return c;
	}

	Color NONE = createMapped(new NoColor(), "NONE", "none", "", "-", "transparent");

	static Color rgba(int r, int g, int b, int a) {
		return new SimpleColor((r << 16) | (g << 8) | b | (a << 24));
	}

	Color BLACK = createMapped(ChatFormatting.BLACK, "BLACK", "black");
	Color DARK_BLUE = createMapped(ChatFormatting.DARK_BLUE, "DARK_BLUE", "dark_blue", "darkBlue");
	Color DARK_GREEN = createMapped(ChatFormatting.DARK_GREEN, "DARK_GREEN", "dark_green", "darkGreen");
	Color DARK_AQUA = createMapped(ChatFormatting.DARK_AQUA, "DARK_AQUA", "dark_aqua", "darkAqua");
	Color DARK_RED = createMapped(ChatFormatting.DARK_RED, "DARK_RED", "dark_red", "darkRed");
	Color DARK_PURPLE = createMapped(ChatFormatting.DARK_PURPLE, "DARK_PURPLE", "dark_purple", "darkPurple");
	Color GOLD = createMapped(ChatFormatting.GOLD, "GOLD", "gold");
	Color GRAY = createMapped(ChatFormatting.GRAY, "GRAY", "gray");
	Color DARK_GRAY = createMapped(ChatFormatting.DARK_GRAY, "DARK_GRAY", "dark_gray", "darkGray");
	Color BLUE = createMapped(ChatFormatting.BLUE, "BLUE", "blue");
	Color GREEN = createMapped(ChatFormatting.GREEN, "GREEN", "green");
	Color AQUA = createMapped(ChatFormatting.AQUA, "AQUA", "aqua");
	Color RED = createMapped(ChatFormatting.RED, "RED", "red");
	Color LIGHT_PURPLE = createMapped(ChatFormatting.LIGHT_PURPLE, "LIGHT_PURPLE", "light_purple", "lightPurple");
	Color YELLOW = createMapped(ChatFormatting.YELLOW, "YELLOW", "yellow");
	Color WHITE = createMapped(ChatFormatting.WHITE, "WHITE", "white");


	Color WHITE_DYE = createMapped(DyeColor.WHITE, "WHITE_DYE", "white_dye", "whiteDye");
	Color ORANGE_DYE = createMapped(DyeColor.ORANGE, "ORANGE_DYE", "orange_dye", "orangeDye");
	Color MAGENTA_DYE = createMapped(DyeColor.MAGENTA, "MAGENTA_DYE", "magenta_dye", "magentaDye");
	Color LIGHT_BLUE_DYE = createMapped(DyeColor.LIGHT_BLUE, "LIGHT_BLUE_DYE", "light_blue_dye", "lightBlueDye");
	Color YELLOW_DYE = createMapped(DyeColor.YELLOW, "YELLOW_DYE", "yellow_dye", "yellowDye");
	Color LIME_DYE = createMapped(DyeColor.LIME, "LIME_DYE", "lime_dye", "limeDye");
	Color PINK_DYE = createMapped(DyeColor.PINK, "PINK_DYE", "pink_dye", "pinkDye");
	Color GRAY_DYE = createMapped(DyeColor.GRAY, "GRAY_DYE", "gray_dye", "grayDye");
	Color LIGHT_GRAY_DYE = createMapped(DyeColor.LIGHT_GRAY, "LIGHT_GRAY_DYE", "lightGrayDye", "lightGrayDye");
	Color CYAN_DYE = createMapped(DyeColor.CYAN, "CYAN_DYE", "cyan_dye", "cyanDye");
	Color PURPLE_DYE = createMapped(DyeColor.PURPLE, "PURPLE_DYE", "purple_dye", "purpleDye");
	Color BLUE_DYE = createMapped(DyeColor.BLUE, "BLUE_DYE", "blue_dye", "blueDye");
	Color BROWN_DYE = createMapped(DyeColor.BROWN, "BROWN_DYE", "brown_dye", "brownDye");
	Color GREEN_DYE = createMapped(DyeColor.GREEN, "GREEN_DYE", "green_dye", "greenDye");
	Color RED_DYE = createMapped(DyeColor.RED, "RED_DYE", "red_dye", "redDye");
	Color BLACK_DYE = createMapped(DyeColor.BLACK, "BLACK_DYE", "black_dye", "blackDye");


}

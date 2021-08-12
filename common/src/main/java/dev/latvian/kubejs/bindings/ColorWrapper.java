package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.util.ColorKJS;
import dev.latvian.kubejs.util.NoColorKJS;
import dev.latvian.kubejs.util.SimpleColorKJS;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.world.item.DyeColor;

import java.util.HashMap;
import java.util.Map;

public interface ColorWrapper {
	Map<String, ColorKJS> MAP = new HashMap<>();

	static ColorKJS of(Object o) {
		if (o instanceof ColorKJS) {
			return (ColorKJS) o;
		} else if (o instanceof String) {
			String s = o.toString();
			ColorKJS c = MAP.get(s);

			if (c != null) {
				return c;
			}

			if (s.startsWith("#")) {
				return new SimpleColorKJS(Integer.decode(s));
			}

			return NONE;
		} else if (o instanceof Number) {
			int i = ((Number) o).intValue();

			if (i == 0) {
				return NONE;
			} else {
				return new SimpleColorKJS(i);
			}
		}

		return NONE;
	}

	static ColorKJS createMapped(Object o, String... names) {
		ColorKJS c = of(o);

		for (String s : names) {
			MAP.put(s, c);
		}

		return c;
	}

	ColorKJS NONE = createMapped(new NoColorKJS(), "NONE", "none", "", "-", "transparent");

	ColorKJS BLACK = createMapped(ChatFormatting.BLACK, "BLACK", "black");
	ColorKJS DARK_BLUE = createMapped(ChatFormatting.DARK_BLUE, "DARK_BLUE", "dark_blue", "darkBlue");
	ColorKJS DARK_GREEN = createMapped(ChatFormatting.DARK_GREEN, "DARK_GREEN", "dark_green", "darkGreen");
	ColorKJS DARK_AQUA = createMapped(ChatFormatting.DARK_AQUA, "DARK_AQUA", "dark_aqua", "darkAqua");
	ColorKJS DARK_RED = createMapped(ChatFormatting.DARK_RED, "DARK_RED", "dark_red", "darkRed");
	ColorKJS DARK_PURPLE = createMapped(ChatFormatting.DARK_PURPLE, "DARK_PURPLE", "dark_purple", "darkPurple");
	ColorKJS GOLD = createMapped(ChatFormatting.GOLD, "GOLD", "gold");
	ColorKJS GRAY = createMapped(ChatFormatting.GRAY, "GRAY", "gray");
	ColorKJS DARK_GRAY = createMapped(ChatFormatting.DARK_GRAY, "DARK_GRAY", "dark_gray", "darkGray");
	ColorKJS BLUE = createMapped(ChatFormatting.BLUE, "BLUE", "blue");
	ColorKJS GREEN = createMapped(ChatFormatting.GREEN, "GREEN", "green");
	ColorKJS AQUA = createMapped(ChatFormatting.AQUA, "AQUA", "aqua");
	ColorKJS RED = createMapped(ChatFormatting.RED, "RED", "red");
	ColorKJS LIGHT_PURPLE = createMapped(ChatFormatting.LIGHT_PURPLE, "LIGHT_PURPLE", "light_purple", "lightPurple");
	ColorKJS YELLOW = createMapped(ChatFormatting.YELLOW, "YELLOW", "yellow");
	ColorKJS WHITE = createMapped(ChatFormatting.WHITE, "WHITE", "white");

	Map<String, ChatFormatting> TEXT = Util.make(new HashMap<>(), map -> {
		for (ChatFormatting c : ChatFormatting.values()) {
			map.put(c.getName(), c);
		}
	});

	ColorKJS WHITE_DYE = createMapped(DyeColor.WHITE, "WHITE_DYE", "white_dye", "whiteDye");
	ColorKJS ORANGE_DYE = createMapped(DyeColor.ORANGE, "ORANGE_DYE", "orange_dye", "orangeDye");
	ColorKJS MAGENTA_DYE = createMapped(DyeColor.MAGENTA, "MAGENTA_DYE", "magenta_dye", "magentaDye");
	ColorKJS LIGHT_BLUE_DYE = createMapped(DyeColor.LIGHT_BLUE, "LIGHT_BLUE_DYE", "light_blue_dye", "lightBlueDye");
	ColorKJS YELLOW_DYE = createMapped(DyeColor.YELLOW, "YELLOW_DYE", "yellow_dye", "yellowDye");
	ColorKJS LIME_DYE = createMapped(DyeColor.LIME, "LIME_DYE", "lime_dye", "limeDye");
	ColorKJS PINK_DYE = createMapped(DyeColor.PINK, "PINK_DYE", "pink_dye", "pinkDye");
	ColorKJS GRAY_DYE = createMapped(DyeColor.GRAY, "GRAY_DYE", "gray_dye", "grayDye");
	ColorKJS LIGHT_GRAY_DYE = createMapped(DyeColor.LIGHT_GRAY, "LIGHT_GRAY_DYE", "lightGrayDye", "lightGrayDye");
	ColorKJS CYAN_DYE = createMapped(DyeColor.CYAN, "CYAN_DYE", "cyan_dye", "cyanDye");
	ColorKJS PURPLE_DYE = createMapped(DyeColor.PURPLE, "PURPLE_DYE", "purple_dye", "purpleDye");
	ColorKJS BLUE_DYE = createMapped(DyeColor.BLUE, "BLUE_DYE", "blue_dye", "blueDye");
	ColorKJS BROWN_DYE = createMapped(DyeColor.BROWN, "BROWN_DYE", "brown_dye", "brownDye");
	ColorKJS GREEN_DYE = createMapped(DyeColor.GREEN, "GREEN_DYE", "green_dye", "greenDye");
	ColorKJS RED_DYE = createMapped(DyeColor.RED, "RED_DYE", "red_dye", "redDye");
	ColorKJS BLACK_DYE = createMapped(DyeColor.BLACK, "BLACK_DYE", "black_dye", "blackDye");

	Map<String, DyeColor> DYE = Util.make(new HashMap<>(), map -> {
		for (DyeColor c : DyeColor.values()) {
			map.put(c.getName(), c);
		}
	});
}

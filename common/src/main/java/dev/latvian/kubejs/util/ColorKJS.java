package dev.latvian.kubejs.util;

import dev.latvian.kubejs.bindings.ColorWrapper;
import dev.latvian.mods.rhino.util.SpecialEquality;
import net.minecraft.network.chat.TextColor;

public interface ColorKJS extends SpecialEquality {
	int getArgbKJS();

	default int getArgbNormalizedKJS() {
		int c = getArgbKJS();
		int a = (c >> 24) & 0xFF;
		return (a == 0) ? (0xFF000000 | c) : c;
	}

	default int getRgbKJS() {
		return getArgbKJS() & 0xFFFFFF;
	}

	default int getFireworkColorKJS() {
		return getRgbKJS();
	}

	default String getHexKJS() {
		int c = getArgbKJS();
		int a = (c >> 24) & 0xFF;
		return a > 0 && a < 255 ? String.format("#%08X", c) : String.format("#%06X", c & 0xFFFFFF);
	}

	default String getSerializeKJS() {
		return getHexKJS();
	}

	default TextColor createTextColorKJS() {
		return TextColor.fromRgb(getRgbKJS());
	}

	@Override
	default boolean specialEquals(Object o, boolean shallow) {
		ColorKJS c = ColorWrapper.of(o);
		return shallow ? (getArgbKJS() == c.getArgbKJS()) : (getRgbKJS() == c.getRgbKJS());
	}
}

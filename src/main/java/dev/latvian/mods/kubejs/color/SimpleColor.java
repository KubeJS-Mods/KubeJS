package dev.latvian.mods.kubejs.color;

import net.minecraft.network.chat.TextColor;

public class SimpleColor implements Color {
	public static final SimpleColor BLACK = new SimpleColor(0xFF000000);
	public static final SimpleColor WHITE = new SimpleColor(0xFFFFFFFF);

	private final int value;
	private TextColor textColor;

	public SimpleColor(int v) {
		value = 0xFF000000 | v;
	}

	@Override
	public int getArgbJS() {
		return value;
	}

	@Override
	public String getHexJS() {
		return String.format("#%06X", getRgbJS());
	}

	@Override
	public String toString() {
		return getHexJS();
	}

	@Override
	public TextColor createTextColorJS() {
		if (textColor == null) {
			textColor = TextColor.fromRgb(getRgbJS());
		}

		return textColor;
	}
}
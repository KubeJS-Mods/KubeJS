package dev.latvian.mods.kubejs.color;

import net.minecraft.network.chat.TextColor;

public class SimpleColorWithAlpha implements Color {
	private final int value;
	private TextColor textColor;

	public SimpleColorWithAlpha(int v) {
		value = v;
	}

	@Override
	public int getArgbJS() {
		return value;
	}

	@Override
	public TextColor createTextColorJS() {
		if (textColor == null) {
			textColor = TextColor.fromRgb(getRgbJS());
		}

		return textColor;
	}

	@Override
	public String toString() {
		return getHexJS();
	}
}
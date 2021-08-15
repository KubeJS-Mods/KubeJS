package dev.latvian.kubejs.util;

import net.minecraft.network.chat.TextColor;

public class SimpleColorWithAlphaKJS implements ColorKJS {
	private final int value;
	private TextColor textColor;

	public SimpleColorWithAlphaKJS(int v) {
		value = v;
	}

	@Override
	public int getArgbKJS() {
		return value;
	}

	@Override
	public TextColor createTextColorKJS() {
		if (textColor == null) {
			textColor = TextColor.fromRgb(getRgbKJS());
		}

		return textColor;
	}

	@Override
	public String toString() {
		return getHexKJS();
	}
}
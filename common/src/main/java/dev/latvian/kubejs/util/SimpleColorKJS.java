package dev.latvian.kubejs.util;

import net.minecraft.network.chat.TextColor;

public class SimpleColorKJS implements ColorKJS {
	public static final SimpleColorKJS BLACK = new SimpleColorKJS(0xFF000000);
	public static final SimpleColorKJS WHITE = new SimpleColorKJS(0xFFFFFFFF);

	private final int value;
	private TextColor textColor;

	public SimpleColorKJS(int v) {
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
}
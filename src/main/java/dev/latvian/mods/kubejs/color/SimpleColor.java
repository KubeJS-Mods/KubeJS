package dev.latvian.mods.kubejs.color;

import net.minecraft.network.chat.TextColor;

public class SimpleColor implements KubeColor {
	public static final SimpleColor BLACK = new SimpleColor(0xFF000000);
	public static final SimpleColor WHITE = new SimpleColor(0xFFFFFFFF);

	private final int value;
	private TextColor textColor;

	public SimpleColor(int v) {
		value = 0xFF000000 | v;
	}

	@Override
	public int kjs$getARGB() {
		return value;
	}

	@Override
	public String kjs$toHexString() {
		return String.format("#%06X", kjs$getRGB());
	}

	@Override
	public String toString() {
		return kjs$toHexString();
	}

	@Override
	public TextColor kjs$createTextColor() {
		if (textColor == null) {
			textColor = TextColor.fromRgb(kjs$getRGB());
		}

		return textColor;
	}
}
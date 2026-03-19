package dev.latvian.mods.kubejs.color;

import net.minecraft.network.chat.TextColor;
import org.jspecify.annotations.Nullable;

public class SimpleColorWithAlpha implements KubeColor {
	private final int value;
	private @Nullable TextColor textColor;

	public SimpleColorWithAlpha(int v) {
		value = v;
	}

	@Override
	public int kjs$getARGB() {
		return value;
	}

	@Override
	public TextColor kjs$createTextColor() {
		if (textColor == null) {
			textColor = TextColor.fromRgb(kjs$getRGB());
		}

		return textColor;
	}

	@Override
	public String toString() {
		return kjs$toHexString();
	}
}
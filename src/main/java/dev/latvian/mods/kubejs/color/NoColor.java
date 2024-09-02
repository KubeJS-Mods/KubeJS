package dev.latvian.mods.kubejs.color;

import net.minecraft.network.chat.TextColor;

public final class NoColor implements KubeColor {
	private static final TextColor TEXT_COLOR = TextColor.fromRgb(0);

	@Override
	public int kjs$getARGB() {
		return 0;
	}

	@Override
	public int kjs$getRGB() {
		return 0;
	}

	@Override
	public String kjs$toHexString() {
		return "#00000000";
	}

	@Override
	public String kjs$serialize() {
		return "none";
	}

	@Override
	public TextColor kjs$createTextColor() {
		return TEXT_COLOR;
	}
}

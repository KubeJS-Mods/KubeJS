package dev.latvian.mods.kubejs.color;

import net.minecraft.network.chat.TextColor;

public final class NoColor implements Color {
	private static final TextColor TEXT_COLOR = TextColor.fromRgb(0);

	@Override
	public int getArgbJS() {
		return 0;
	}

	@Override
	public int getRgbJS() {
		return 0;
	}

	@Override
	public String getHexJS() {
		return "#00000000";
	}

	@Override
	public String getSerializeJS() {
		return "none";
	}

	@Override
	public TextColor createTextColorJS() {
		return TEXT_COLOR;
	}
}

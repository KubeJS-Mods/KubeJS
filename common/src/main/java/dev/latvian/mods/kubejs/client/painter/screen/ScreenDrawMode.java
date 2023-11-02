package dev.latvian.mods.kubejs.client.painter.screen;

public enum ScreenDrawMode {
	ALWAYS,
	INGAME,
	GUI;

	public boolean ingame() {
		return this == ALWAYS || this == INGAME;
	}

	public boolean gui() {
		return this == ALWAYS || this == GUI;
	}
}

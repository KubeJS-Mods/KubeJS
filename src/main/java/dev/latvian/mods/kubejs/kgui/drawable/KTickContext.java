package dev.latvian.mods.kubejs.kgui.drawable;

import net.minecraft.client.gui.Font;

public record KTickContext(
	// KGUI gui,
	Font font,
	int mx,
	int my,
	int mxPress,
	int myPress
) {
	public boolean isMouseOver(int x, int y, int w, int h) {
		return mx >= x && my >= y && mx < x + w && my < y + h;
	}

	public Boolean respond(String id) {
		return Boolean.TRUE;
	}
}

package dev.latvian.mods.kubejs.kgui.drawable;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public record KDrawContext(
	KTickContext tick,
	GuiGraphics graphics,
	float delta
) {
	public static final int FLAG_SHADOW = 1;
	public static final int FLAG_CENTER_X = 2;
	public static final int FLAG_CENTER_Y = 4;

	public void text(@Nullable String string, int x, int y, int color, int flags) {
		if (string == null || string.isBlank()) {
			return;
		}

		if ((flags & FLAG_CENTER_X) != 0) {
			x -= tick.font().width(string) / 2;
		}

		if ((flags & FLAG_CENTER_Y) != 0) {
			y -= tick.font().lineHeight / 2;
		}

		graphics.drawString(tick.font(), string, x, y, color, (flags & FLAG_SHADOW) != 0);
	}

	public void text(@Nullable Component component, int x, int y, int color, int flags) {
		if (component == null) {
			return;
		}

		if ((flags & FLAG_CENTER_X) != 0) {
			x -= tick.font().width(component) / 2;
		}

		if ((flags & FLAG_CENTER_Y) != 0) {
			y -= tick.font().lineHeight / 2;
		}

		graphics.drawString(tick.font(), component, x, y, color, (flags & FLAG_SHADOW) != 0);
	}
}

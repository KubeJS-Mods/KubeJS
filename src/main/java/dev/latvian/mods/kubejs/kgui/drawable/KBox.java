package dev.latvian.mods.kubejs.kgui.drawable;

import dev.latvian.mods.kubejs.kgui.KColorScheme;

public record KBox(int depth, KColorScheme colorScheme, KDrawable outline) implements KDrawable {
	@Override
	public void draw(KDrawContext ctx, int x, int y, int w, int h) {
		outline.draw(ctx, x, y, w, h);
		colorScheme.background.draw(ctx, x + 1, y + h - depth - 1, w - 2, depth);
		colorScheme.border.draw(ctx, x + 1, y + 1, w - 2, h - 2 - depth);
		colorScheme.fill.draw(ctx, x + 2, y + 2, w - 4, h - 4 - depth);
	}
}

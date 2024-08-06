package dev.latvian.mods.kubejs.kgui.drawable;

import dev.latvian.mods.kubejs.color.Color;
import dev.latvian.mods.kubejs.color.SimpleColorWithAlpha;

public record KColor(Color color) implements KDrawable {
	public static KColor of(int col) {
		return new KColor(new SimpleColorWithAlpha(col));
	}

	public static final KColor BLACK = of(0xFF000000);
	public static final KColor WHITE = of(0xFFFFFFFF);
	public static final KColor DARK_GRAY = of(0xFF4C4C4C);

	@Override
	public void draw(KDrawContext ctx, int x, int y, int w, int h) {
		if (w > 0 && h > 0) {
			ctx.graphics().fill(x, y, x + w, y + h, color.getArgbJS());
		}
	}
}

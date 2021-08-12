package dev.latvian.kubejs.client.painter.screen;

import dev.latvian.kubejs.client.painter.Painter;
import dev.latvian.kubejs.client.painter.PainterObject;
import dev.latvian.kubejs.client.painter.PainterObjectProperties;
import org.intellij.lang.annotations.MagicConstant;

public abstract class ScreenPainterObject extends PainterObject {
	public float x = 0F;
	public float y = 0F;
	public float z = 0F;
	public float w = 16F;
	public float h = 16F;

	@MagicConstant(intValues = {Painter.LEFT, Painter.CENTER, Painter.RIGHT})
	public int alignX = Painter.LEFT;

	@MagicConstant(intValues = {Painter.TOP, Painter.CENTER, Painter.BOTTOM})
	public int alignY = Painter.TOP;

	@MagicConstant(intValues = {Painter.DRAW_ALWAYS, Painter.DRAW_INGAME, Painter.DRAW_GUI})
	public int draw = Painter.DRAW_INGAME;

	public void preDraw(ScreenPaintEventJS event) {
	}

	public abstract void draw(ScreenPaintEventJS event);

	@Override
	protected void load(PainterObjectProperties properties) {
		super.load(properties);

		x = properties.getFloat("x", x);
		y = properties.getFloat("y", y);
		z = properties.getFloat("y", z);
		w = properties.getFloat("w", w);
		h = properties.getFloat("h", h);

		x += properties.getFloat("moveX", 0F);
		y += properties.getFloat("moveY", 0F);
		w += properties.getFloat("expandW", 0F);
		h += properties.getFloat("expandH", 0F);

		if (properties.hasString("draw")) {
			switch (properties.getString("draw", "ingame")) {
				case "always":
					draw = Painter.DRAW_ALWAYS;
					break;
				case "gui":
					draw = Painter.DRAW_GUI;
					break;
				default:
					draw = Painter.DRAW_INGAME;
					break;
			}
		}

		if (properties.hasString("alignX")) {
			switch (properties.getString("alignX", "left")) {
				case "right":
					alignX = Painter.RIGHT;
					break;
				case "center":
					alignX = Painter.CENTER;
					break;
				default:
					alignX = Painter.LEFT;
					break;
			}
		}

		if (properties.hasString("alignY")) {
			switch (properties.getString("alignY", "top")) {
				case "bottom":
					alignY = Painter.BOTTOM;
					break;
				case "center":
					alignY = Painter.CENTER;
					break;
				default:
					alignY = Painter.TOP;
					break;
			}
		}
	}
}

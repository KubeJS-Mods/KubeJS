package dev.latvian.mods.kubejs.client.painter.screen;

import dev.latvian.mods.kubejs.client.painter.Painter;
import dev.latvian.mods.kubejs.client.painter.PainterObject;
import dev.latvian.mods.kubejs.client.painter.PainterObjectProperties;
import dev.latvian.mods.unit.FixedNumberUnit;
import dev.latvian.mods.unit.Unit;
import org.intellij.lang.annotations.MagicConstant;

public abstract class ScreenPainterObject extends PainterObject {
	private static final Unit DEFAULT_SIZE = FixedNumberUnit.SIXTEEN;

	public Unit x = FixedNumberUnit.ZERO;
	public Unit y = FixedNumberUnit.ZERO;
	public Unit z = FixedNumberUnit.ZERO;
	public Unit w = DEFAULT_SIZE;
	public Unit h = DEFAULT_SIZE;

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

		x = properties.getUnit("x", x).add(properties.getUnit("moveX", FixedNumberUnit.ZERO));
		y = properties.getUnit("y", y).add(properties.getUnit("moveY", FixedNumberUnit.ZERO));
		z = properties.getUnit("z", z);
		w = properties.getUnit("w", w).add(properties.getUnit("expandW", FixedNumberUnit.ZERO));
		h = properties.getUnit("h", h).add(properties.getUnit("expandH", FixedNumberUnit.ZERO));

		if (properties.hasString("draw")) {
			switch (properties.getString("draw", "ingame")) {
				case "always" -> draw = Painter.DRAW_ALWAYS;
				case "gui" -> draw = Painter.DRAW_GUI;
				default -> draw = Painter.DRAW_INGAME;
			}
		}

		if (properties.hasString("alignX")) {
			switch (properties.getString("alignX", "left")) {
				case "right" -> alignX = Painter.RIGHT;
				case "center" -> alignX = Painter.CENTER;
				default -> alignX = Painter.LEFT;
			}
		}

		if (properties.hasString("alignY")) {
			switch (properties.getString("alignY", "top")) {
				case "bottom" -> alignY = Painter.BOTTOM;
				case "center" -> alignY = Painter.CENTER;
				default -> alignY = Painter.TOP;
			}
		}
	}
}

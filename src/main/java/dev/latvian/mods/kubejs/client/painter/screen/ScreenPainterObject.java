package dev.latvian.mods.kubejs.client.painter.screen;

import dev.latvian.mods.kubejs.client.painter.PainterObject;
import dev.latvian.mods.kubejs.client.painter.PainterObjectProperties;
import dev.latvian.mods.unit.FixedNumberUnit;
import dev.latvian.mods.unit.Unit;
import net.minecraft.core.HolderLookup;

public abstract class ScreenPainterObject extends PainterObject {
	public Unit x = FixedNumberUnit.ZERO;
	public Unit y = FixedNumberUnit.ZERO;
	public Unit z = FixedNumberUnit.ZERO;
	public ScreenDrawMode draw = ScreenDrawMode.INGAME;

	public void preDraw(PaintScreenKubeEvent event) {
	}

	public abstract void draw(PaintScreenKubeEvent event);

	@Override
	protected void load(HolderLookup.Provider registries, PainterObjectProperties properties) {
		super.load(registries, properties);

		x = properties.getUnit("x", x).add(properties.getUnit("moveX", FixedNumberUnit.ZERO));
		y = properties.getUnit("y", y).add(properties.getUnit("moveY", FixedNumberUnit.ZERO));
		z = properties.getUnit("z", z);

		if (properties.hasString("draw")) {
			switch (properties.getString("draw", "ingame")) {
				case "always" -> draw = ScreenDrawMode.ALWAYS;
				case "gui" -> draw = ScreenDrawMode.GUI;
				default -> draw = ScreenDrawMode.INGAME;
			}
		}
	}
}

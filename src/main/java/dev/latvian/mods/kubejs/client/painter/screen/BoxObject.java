package dev.latvian.mods.kubejs.client.painter.screen;

import dev.latvian.mods.kubejs.client.painter.Painter;
import dev.latvian.mods.kubejs.client.painter.PainterObjectProperties;
import dev.latvian.mods.unit.FixedNumberUnit;
import dev.latvian.mods.unit.Unit;
import net.minecraft.core.HolderLookup;

public abstract class BoxObject extends ScreenPainterObject {
	private static final Unit DEFAULT_SIZE = FixedNumberUnit.SIXTEEN;

	public Unit w = DEFAULT_SIZE;
	public Unit h = DEFAULT_SIZE;
	public AlignMode alignX = AlignMode.START;
	public AlignMode alignY = AlignMode.START;

	public BoxObject(Painter painter) {
	}

	@Override
	protected void load(HolderLookup.Provider registries, PainterObjectProperties properties) {
		super.load(registries, properties);

		w = properties.getUnit("w", w).add(properties.getUnit("expandW", FixedNumberUnit.ZERO));
		h = properties.getUnit("h", h).add(properties.getUnit("expandH", FixedNumberUnit.ZERO));

		if (properties.hasString("alignX")) {
			switch (properties.getString("alignX", "left")) {
				case "right", "end" -> alignX = AlignMode.END;
				case "center" -> alignX = AlignMode.CENTER;
				default -> alignX = AlignMode.START;
			}
		}

		if (properties.hasString("alignY")) {
			switch (properties.getString("alignY", "top")) {
				case "bottom", "end" -> alignY = AlignMode.END;
				case "center" -> alignY = AlignMode.CENTER;
				default -> alignY = AlignMode.START;
			}
		}
	}
}

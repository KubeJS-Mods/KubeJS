package dev.latvian.kubejs.client.painter.screen;

import dev.latvian.kubejs.client.painter.PainterObject;
import dev.latvian.kubejs.client.painter.PainterObjectProperties;
import dev.latvian.kubejs.client.painter.PainterObjectStorage;
import dev.latvian.mods.rhino.util.unit.FixedUnit;
import dev.latvian.mods.rhino.util.unit.Unit;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class ScreenGroup extends ScreenPainterObject {
	private final PainterObjectStorage storage = new PainterObjectStorage();
	private float scaleX = 1F;
	private float scaleY = 1F;
	private float scaleZ = 1F;
	private Unit paddingW = Unit.ZERO;
	private Unit paddingH = Unit.ZERO;

	@Override
	protected void load(PainterObjectProperties properties) {
		super.load(properties);

		Tag c = properties.tag.get("children");

		if (c instanceof CompoundTag) {
			storage.handle((CompoundTag) c);
		}

		scaleX = properties.getFloat("scaleX", scaleX);
		scaleY = properties.getFloat("scaleY", scaleY);
		scaleZ = properties.getFloat("scaleZ", scaleZ);
		paddingW = properties.getUnit("paddingW", paddingW);
		paddingH = properties.getUnit("paddingH", paddingH);

		if (properties.hasNumber("scale")) {
			scaleX = scaleY = properties.getFloat("scale", 1F);
		}
	}

	@Override
	public void preDraw(ScreenPaintEventJS event) {
		w = FixedUnit.ZERO;
		h = FixedUnit.ZERO;

		for (PainterObject object : storage.getObjects()) {
			if (object instanceof ScreenPainterObject) {
				ScreenPainterObject s = (ScreenPainterObject) object;
				s.preDraw(event);
				w = w.max(s.x.add(s.w));
				h = h.max(s.y.add(s.h));
			}
		}

		w = w.add(paddingW);
		h = w.add(paddingH);
	}

	@Override
	public void draw(ScreenPaintEventJS event) {
		float ax = event.alignX(x.get(), w.get(), alignX);
		float ay = event.alignY(y.get(), h.get(), alignY);
		float az = z.get();

		event.push();
		event.translate(ax, ay, az);
		event.scale(scaleX, scaleY, scaleZ);

		for (PainterObject object : storage.getObjects()) {
			if (object instanceof ScreenPainterObject) {
				ScreenPainterObject s = (ScreenPainterObject) object;
				s.draw(event);
			}
		}

		event.pop();
	}
}

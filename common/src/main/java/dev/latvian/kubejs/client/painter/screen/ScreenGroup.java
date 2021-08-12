package dev.latvian.kubejs.client.painter.screen;

import dev.latvian.kubejs.client.painter.PainterObject;
import dev.latvian.kubejs.client.painter.PainterObjectProperties;
import dev.latvian.kubejs.client.painter.PainterObjectStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class ScreenGroup extends ScreenPainterObject {
	private final PainterObjectStorage storage = new PainterObjectStorage();
	private float scaleX = 1F;
	private float scaleY = 1F;
	private float scaleZ = 1F;
	private float paddingW = 0F;
	private float paddingH = 0F;

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
		paddingW = properties.getFloat("paddingW", paddingW);
		paddingH = properties.getFloat("paddingH", paddingH);

		if (properties.hasNumber("scale")) {
			scaleX = scaleY = properties.getFloat("scale", 1F);
		}
	}

	@Override
	public void preDraw(ScreenPaintEventJS event) {
		w = 0F;
		h = 0F;

		for (PainterObject object : storage.getObjects()) {
			if (object instanceof ScreenPainterObject) {
				ScreenPainterObject s = (ScreenPainterObject) object;
				s.preDraw(event);
				w = Math.max(w, s.x + s.w);
				h = Math.max(h, s.y + s.h);
			}
		}

		w += paddingW;
		h += paddingH;
	}

	@Override
	public void draw(ScreenPaintEventJS event) {
		float ax = event.alignX(x, w, alignX);
		float ay = event.alignY(y, h, alignY);

		event.push();
		event.translate(ax, ay, z);
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

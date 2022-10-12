package dev.latvian.mods.kubejs.client.painter.screen;

import dev.latvian.mods.kubejs.client.painter.Painter;
import dev.latvian.mods.kubejs.client.painter.PainterObjectProperties;
import dev.latvian.mods.kubejs.client.painter.PainterObjectStorage;
import dev.latvian.mods.unit.FixedNumberUnit;
import dev.latvian.mods.unit.Unit;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;

public class ScreenGroup extends ScreenPainterObject {
	private final PainterObjectStorage storage;
	private Unit scaleX = FixedNumberUnit.ONE;
	private Unit scaleY = FixedNumberUnit.ONE;
	private Unit paddingW = FixedNumberUnit.ZERO;
	private Unit paddingH = FixedNumberUnit.ZERO;

	public ScreenGroup(Painter painter) {
		storage = new PainterObjectStorage(painter);
	}

	@Override
	protected void load(PainterObjectProperties properties) {
		super.load(properties);

		var c = properties.tag.get("children");

		if (c instanceof CompoundTag tag) {
			storage.handle(tag);
		}

		paddingW = properties.getUnit("paddingW", paddingW);
		paddingH = properties.getUnit("paddingH", paddingH);

		if (properties.hasAny("scale")) {
			scaleX = scaleY = properties.getUnit("scale", FixedNumberUnit.ONE);
		} else {
			scaleX = properties.getUnit("scaleX", scaleX);
			scaleY = properties.getUnit("scaleY", scaleY);
		}
	}

	@Override
	public void preDraw(PaintScreenEventJS event) {
		w = FixedNumberUnit.ZERO;
		h = FixedNumberUnit.ZERO;
		var objects = storage.getObjects();

		if (objects.isEmpty()) {
			return;
		}

		List<Unit> wunits = new ArrayList<>(objects.size());
		List<Unit> hunits = new ArrayList<>(objects.size());

		for (var object : objects) {
			if (object instanceof ScreenPainterObject s) {
				s.preDraw(event);
				wunits.add(s.x.add(s.w));
				hunits.add(s.y.add(s.h));
			}
		}

		w = new MultiMaxFunc(wunits).add(paddingW);
		h = new MultiMaxFunc(hunits).add(paddingH);
	}

	@Override
	public void draw(PaintScreenEventJS event) {
		var ax = event.alignX(x.getFloat(event), w.getFloat(event), alignX);
		var ay = event.alignY(y.getFloat(event), h.getFloat(event), alignY);
		var az = z.getFloat(event);

		event.push();
		event.translate(ax, ay, az);
		event.scale(scaleX.getFloat(event), scaleY.getFloat(event), 1F);

		for (var object : storage.getObjects()) {
			if (object instanceof ScreenPainterObject s) {
				s.draw(event);
			}
		}

		event.pop();
	}
}

package dev.latvian.mods.kubejs.client.painter.screen;

import dev.latvian.mods.kubejs.client.painter.Painter;
import dev.latvian.mods.kubejs.client.painter.PainterObjectProperties;
import dev.latvian.mods.kubejs.client.painter.PainterObjectStorage;
import dev.latvian.mods.unit.FixedNumberUnit;
import dev.latvian.mods.unit.Unit;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;

public class ScreenGroup extends BoxObject {
	public final PainterObjectStorage storage;
	public Unit scaleX = FixedNumberUnit.ONE;
	public Unit scaleY = FixedNumberUnit.ONE;
	public Unit paddingW = FixedNumberUnit.ZERO;
	public Unit paddingH = FixedNumberUnit.ZERO;

	public ScreenGroup(Painter painter) {
		super(painter);
		storage = new PainterObjectStorage(painter);
	}

	@Override
	protected void load(HolderLookup.Provider registries, PainterObjectProperties properties) {
		super.load(registries, properties);

		var c = properties.tag.get("children");

		if (c instanceof CompoundTag tag) {
			storage.handle(registries, tag);
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
	public void preDraw(PaintScreenKubeEvent event) {
		w = FixedNumberUnit.ZERO;
		h = FixedNumberUnit.ZERO;
		var objects = storage.getObjects();

		if (objects.isEmpty()) {
			return;
		}

		var wunits = new ArrayList<Unit>(objects.size());
		var hunits = new ArrayList<Unit>(objects.size());

		for (var object : objects) {
			if (object instanceof ScreenPainterObject s) {
				s.preDraw(event);
			}

			if (object instanceof BoxObject s) {
				wunits.add(s.x.add(s.w));
				hunits.add(s.y.add(s.h));
			}
		}

		w = new MultiMaxFunc(wunits).add(paddingW);
		h = new MultiMaxFunc(hunits).add(paddingH);
	}

	@Override
	public void draw(PaintScreenKubeEvent event) {
		var ax = event.alignX(x.getFloat(event), w.getFloat(event), alignX);
		var ay = event.alignY(y.getFloat(event), h.getFloat(event), alignY);
		var az = z.getFloat(event);

		event.push();
		event.translate(ax, ay, az);

		if (scaleX != FixedNumberUnit.ONE || scaleY != FixedNumberUnit.ONE) {
			event.scale(scaleX.getFloat(event), scaleY.getFloat(event), 1F);
		}

		for (var object : storage.getObjects()) {
			if (object instanceof ScreenPainterObject s) {
				s.draw(event);
			}
		}

		event.pop();
	}
}

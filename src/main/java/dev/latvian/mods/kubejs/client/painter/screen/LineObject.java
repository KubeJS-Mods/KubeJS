package dev.latvian.mods.kubejs.client.painter.screen;

import dev.latvian.mods.kubejs.client.painter.Painter;
import dev.latvian.mods.kubejs.client.painter.PainterObjectProperties;
import dev.latvian.mods.unit.FixedColorUnit;
import dev.latvian.mods.unit.FixedNumberUnit;
import dev.latvian.mods.unit.Unit;
import net.minecraft.core.HolderLookup;

public class LineObject extends ScreenPainterObject {
	public Unit color = FixedColorUnit.WHITE;
	public Unit x2 = FixedNumberUnit.ZERO;
	public Unit y2 = FixedNumberUnit.ZERO;
	public Unit size = FixedNumberUnit.ZERO;
	public Unit length = FixedNumberUnit.ZERO;
	public Unit rotation = FixedNumberUnit.ZERO;
	public Unit offset = FixedNumberUnit.ZERO;

	public LineObject(Painter painter) {
	}

	@Override
	protected void load(HolderLookup.Provider registries, PainterObjectProperties properties) {
		super.load(registries, properties);

		color = properties.getColor("color", color);
		x2 = properties.getUnit("x2", x2);
		y2 = properties.getUnit("y2", y2);
		size = properties.getUnit("size", size);
		length = properties.getUnit("length", length);
		rotation = properties.getUnit("rotation", rotation);
		offset = properties.getUnit("offset", offset);
	}

	@Override
	public void draw(PaintScreenKubeEvent event) {
		var ax = x.getFloat(event);
		var ay = y.getFloat(event);
		var az = z.getFloat(event);
		var as = (size == FixedNumberUnit.ZERO ? event.painter.defaultLineSizeUnit : size).getFloat(event);

		var alength = length.getFloat(event);
		var aangle = rotation.getFloat(event);

		if (alength <= 0.0001F) {
			if (x2 == FixedNumberUnit.ZERO && y2 == FixedNumberUnit.ZERO) {
				return;
			}

			var ax2 = x2.getFloat(event);
			var ay2 = y2.getFloat(event);
			alength = (float) Math.sqrt((ax2 - ax) * (ax2 - ax) + (ay2 - ay) * (ay2 - ay));
			aangle = (float) Math.toDegrees(Math.atan2(ay2 - ay, ax2 - ax));
		}

		event.push();
		event.translate(ax, ay);
		event.rotateDeg(aangle);
		event.setPositionColorShader();
		event.blend(true);
		event.beginQuads(false);
		event.rectangle(offset.getFloat(event), -as / 2F, az, alength, as, color.getInt(event));
		event.end();
		event.pop();
	}
}

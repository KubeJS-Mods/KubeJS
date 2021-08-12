package dev.latvian.kubejs.client.painter.world;

import dev.latvian.kubejs.client.painter.PainterObject;
import dev.latvian.kubejs.client.painter.PainterObjectProperties;

public abstract class WorldPainterObject extends PainterObject {
	public double x = 0F;
	public double y = 0F;
	public double z = 0F;
	public double w = 1D;
	public double h = 1D;
	public double d = 1D;

	public void preDraw(WorldPaintEventJS event) {
	}

	public abstract void draw(WorldPaintEventJS event);

	@Override
	protected void load(PainterObjectProperties properties) {
		super.load(properties);

		x = properties.getDouble("x", x);
		y = properties.getDouble("y", y);
		z = properties.getDouble("y", z);
		w = properties.getDouble("w", w);
		h = properties.getDouble("h", h);
		h = properties.getDouble("d", d);

		x += properties.getDouble("moveX", 0D);
		y += properties.getDouble("moveY", 0D);
		z += properties.getDouble("moveZ", 0D);
		w += properties.getDouble("expandW", 0D);
		h += properties.getDouble("expandH", 0D);
		d += properties.getDouble("expandD", 0D);
	}
}

package dev.latvian.mods.kubejs.client.painter.screen;

import dev.latvian.mods.kubejs.client.painter.PainterObjectProperties;
import dev.latvian.mods.rhino.util.unit.FixedUnit;
import dev.latvian.mods.rhino.util.unit.Unit;
import net.minecraft.resources.ResourceLocation;

public class RectangleObject extends ScreenPainterObject {
	private Unit color = PainterObjectProperties.WHITE_COLOR;
	private ResourceLocation texture = null;
	private Unit u0 = FixedUnit.ZERO;
	private Unit v0 = FixedUnit.ZERO;
	private Unit u1 = FixedUnit.ONE;
	private Unit v1 = FixedUnit.ONE;

	@Override
	protected void load(PainterObjectProperties properties) {
		super.load(properties);

		color = properties.getColor("color", color);
		texture = properties.getResourceLocation("texture", texture);
		u0 = properties.getUnit("u0", u0);
		v0 = properties.getUnit("v0", v0);
		u1 = properties.getUnit("u1", u1);
		v1 = properties.getUnit("v1", v1);
	}

	@Override
	public void draw(ScreenPaintEventJS event) {
		var aw = w.get();
		var ah = h.get();
		var ax = event.alignX(x.get(), aw, alignX);
		var ay = event.alignY(y.get(), ah, alignY);
		var az = z.get();

		if (texture == null) {
			event.setPositionColorShader();
			event.beginQuads(false);
			event.rectangle(ax, ay, az, aw, ah, color.getAsInt());
			event.end();
		} else {
			float u0f = u0.get();
			float v0f = v0.get();
			float u1f = u1.get();
			float v1f = v1.get();

			event.setPositionColorTextureShader();
			event.setShaderTexture(texture);
			event.beginQuads(true);
			event.rectangle(ax, ay, az, aw, ah, color.getAsInt(), u0f, v0f, u1f, v1f);
			event.end();
		}
	}
}

package dev.latvian.mods.kubejs.client.painter.screen;

import dev.latvian.mods.kubejs.client.painter.PainterObjectProperties;
import dev.latvian.mods.rhino.util.unit.FixedUnit;
import dev.latvian.mods.rhino.util.unit.Unit;
import net.minecraft.resources.ResourceLocation;

public class GradientObject extends ScreenPainterObject {
	private Unit colorTL = PainterObjectProperties.WHITE_COLOR;
	private Unit colorTR = PainterObjectProperties.WHITE_COLOR;
	private Unit colorBL = PainterObjectProperties.WHITE_COLOR;
	private Unit colorBR = PainterObjectProperties.WHITE_COLOR;
	private ResourceLocation texture = null;
	private Unit u0 = FixedUnit.ZERO;
	private Unit v0 = FixedUnit.ZERO;
	private Unit u1 = FixedUnit.ONE;
	private Unit v1 = FixedUnit.ONE;

	@Override
	protected void load(PainterObjectProperties properties) {
		super.load(properties);
		colorTL = properties.getColor("colorTL", colorTL);
		colorTR = properties.getColor("colorTR", colorTR);
		colorBL = properties.getColor("colorBL", colorBL);
		colorBR = properties.getColor("colorBR", colorBR);

		if (properties.hasAny("colorT")) {
			colorTL = colorTR = properties.getColor("colorT", PainterObjectProperties.WHITE_COLOR);
		}

		if (properties.hasAny("colorB")) {
			colorBL = colorBR = properties.getColor("colorB", PainterObjectProperties.WHITE_COLOR);
		}

		if (properties.hasAny("colorL")) {
			colorTL = colorBL = properties.getColor("colorL", PainterObjectProperties.WHITE_COLOR);
		}

		if (properties.hasAny("colorR")) {
			colorTR = colorBR = properties.getColor("colorR", PainterObjectProperties.WHITE_COLOR);
		}

		if (properties.hasAny("color")) {
			colorTL = colorTR = colorBL = colorBR = properties.getColor("color", PainterObjectProperties.WHITE_COLOR);
		}

		texture = properties.getResourceLocation("texture", texture);
		u0 = properties.getUnit("u0", u0);
		v0 = properties.getUnit("v0", v0);
		u1 = properties.getUnit("u1", u1);
		v1 = properties.getUnit("v1", v1);
	}

	@Override
	public void draw(ScreenPaintEventJS event) {
		var colBL = colorBL.getAsInt();
		var colBR = colorBR.getAsInt();
		var colTR = colorTR.getAsInt();
		var colTL = colorTL.getAsInt();

		if (((colBL >> 24) & 0xFF) < 2 && ((colBR >> 24) & 0xFF) < 2 && ((colTR >> 24) & 0xFF) < 2 && ((colTL >> 24) & 0xFF) < 2) {
			return;
		}

		var aw = w.get();
		var ah = h.get();
		var ax = event.alignX(x.get(), aw, alignX);
		var ay = event.alignY(y.get(), ah, alignY);
		var az = z.get();
		var m = event.getMatrix();

		if (texture == null) {
			event.setPositionColorShader();
			event.beginQuads(false);
			event.vertex(m, ax + aw, ay, az, colTR);
			event.vertex(m, ax, ay, az, colTL);
			event.vertex(m, ax, ay + ah, az, colBL);
			event.vertex(m, ax + aw, ay + ah, az, colBR);
			event.end();
		} else {
			float u0f = u0.get();
			float v0f = v0.get();
			float u1f = u1.get();
			float v1f = v1.get();

			event.setPositionColorTextureShader();
			event.setShaderTexture(texture);
			event.beginQuads(true);
			event.vertex(m, ax + aw, ay, az, colTR, u1f, v0f);
			event.vertex(m, ax, ay, az, colTL, u0f, v0f);
			event.vertex(m, ax, ay + ah, az, colBL, u0f, v1f);
			event.vertex(m, ax + aw, ay + ah, az, colBR, u1f, v1f);
			event.end();
		}

	}
}

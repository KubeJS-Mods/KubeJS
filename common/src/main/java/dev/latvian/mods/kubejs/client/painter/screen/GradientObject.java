package dev.latvian.mods.kubejs.client.painter.screen;

import dev.latvian.mods.kubejs.client.painter.PainterObjectProperties;
import dev.latvian.mods.unit.FixedColorUnit;
import dev.latvian.mods.unit.FixedNumberUnit;
import dev.latvian.mods.unit.Unit;
import net.minecraft.resources.ResourceLocation;

public class GradientObject extends ScreenPainterObject {
	private Unit colorTL = FixedColorUnit.WHITE;
	private Unit colorTR = FixedColorUnit.WHITE;
	private Unit colorBL = FixedColorUnit.WHITE;
	private Unit colorBR = FixedColorUnit.WHITE;
	private ResourceLocation texture = null;
	private Unit u0 = FixedNumberUnit.ZERO;
	private Unit v0 = FixedNumberUnit.ZERO;
	private Unit u1 = FixedNumberUnit.ONE;
	private Unit v1 = FixedNumberUnit.ONE;

	@Override
	protected void load(PainterObjectProperties properties) {
		super.load(properties);
		colorTL = properties.getColor("colorTL", colorTL);
		colorTR = properties.getColor("colorTR", colorTR);
		colorBL = properties.getColor("colorBL", colorBL);
		colorBR = properties.getColor("colorBR", colorBR);

		if (properties.hasAny("colorT")) {
			colorTL = colorTR = properties.getColor("colorT", FixedColorUnit.WHITE);
		}

		if (properties.hasAny("colorB")) {
			colorBL = colorBR = properties.getColor("colorB", FixedColorUnit.WHITE);
		}

		if (properties.hasAny("colorL")) {
			colorTL = colorBL = properties.getColor("colorL", FixedColorUnit.WHITE);
		}

		if (properties.hasAny("colorR")) {
			colorTR = colorBR = properties.getColor("colorR", FixedColorUnit.WHITE);
		}

		if (properties.hasAny("color")) {
			colorTL = colorTR = colorBL = colorBR = properties.getColor("color", FixedColorUnit.WHITE);
		}

		texture = properties.getResourceLocation("texture", texture);
		u0 = properties.getUnit("u0", u0);
		v0 = properties.getUnit("v0", v0);
		u1 = properties.getUnit("u1", u1);
		v1 = properties.getUnit("v1", v1);
	}

	@Override
	public void draw(ScreenPaintEventJS event) {
		var colBL = colorBL.getInt(event);
		var colBR = colorBR.getInt(event);
		var colTR = colorTR.getInt(event);
		var colTL = colorTL.getInt(event);

		if (((colBL >> 24) & 0xFF) < 2 && ((colBR >> 24) & 0xFF) < 2 && ((colTR >> 24) & 0xFF) < 2 && ((colTL >> 24) & 0xFF) < 2) {
			return;
		}

		var aw = w.getFloat(event);
		var ah = h.getFloat(event);
		var ax = event.alignX(x.getFloat(event), aw, alignX);
		var ay = event.alignY(y.getFloat(event), ah, alignY);
		var az = z.getFloat(event);
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
			float u0f = u0.getFloat(event);
			float v0f = v0.getFloat(event);
			float u1f = u1.getFloat(event);
			float v1f = v1.getFloat(event);

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

package dev.latvian.kubejs.client.painter.screen;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.math.Matrix4f;
import dev.latvian.kubejs.client.painter.PainterObjectProperties;
import dev.latvian.mods.rhino.util.unit.Unit;
import net.minecraft.resources.ResourceLocation;

public class GradientObject extends ScreenPainterObject {
	private Unit colorTL = PainterObjectProperties.WHITE_COLOR;
	private Unit colorTR = PainterObjectProperties.WHITE_COLOR;
	private Unit colorBL = PainterObjectProperties.WHITE_COLOR;
	private Unit colorBR = PainterObjectProperties.WHITE_COLOR;
	private ResourceLocation texture = null;
	private float u0 = 0F;
	private float v0 = 0F;
	private float u1 = 1F;
	private float v1 = 1F;

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
		u0 = properties.getFloat("u0", u0);
		v0 = properties.getFloat("v0", v0);
		u1 = properties.getFloat("u1", u1);
		v1 = properties.getFloat("v1", v1);
	}

	@Override
	public void draw(ScreenPaintEventJS event) {
		int colBL = colorBL.getAsInt();
		int colBR = colorBR.getAsInt();
		int colTR = colorTR.getAsInt();
		int colTL = colorTL.getAsInt();

		if (((colBL >> 24) & 0xFF) < 2 && ((colBR >> 24) & 0xFF) < 2 && ((colTR >> 24) & 0xFF) < 2 && ((colTL >> 24) & 0xFF) < 2) {
			return;
		}

		float aw = w.get();
		float ah = h.get();
		float ax = event.alignX(x.get(), aw, alignX);
		float ay = event.alignY(y.get(), ah, alignY);
		float az = z.get();
		Matrix4f m = event.getMatrix();

		event.setSmoothShade(true);
		// FIXME: possibly no longer required in 1.17
		// RenderSystem.alphaFunc(GL11.GL_GREATER, 0.003F);

		if (texture == null) {
			event.setTextureEnabled(false);
			event.beginQuads(DefaultVertexFormat.POSITION_COLOR);
			event.vertex(m, ax, ay + ah, az, colBL);
			event.vertex(m, ax + aw, ay + ah, az, colBR);
			event.vertex(m, ax + aw, ay, az, colTR);
			event.vertex(m, ax, ay, az, colTL);
			event.end();
			event.setTextureEnabled(true);
		} else {
			event.bindTexture(texture);
			event.beginQuads(DefaultVertexFormat.POSITION_COLOR_TEX);
			event.vertex(m, ax, ay + ah, az, colBL, u0, v1);
			event.vertex(m, ax + aw, ay + ah, az, colBR, u1, v1);
			event.vertex(m, ax + aw, ay, az, colTR, u1, v0);
			event.vertex(m, ax, ay, az, colTL, u0, v0);
			event.end();
		}

		event.setSmoothShade(false);
		// RenderSystem.defaultAlphaFunc();
	}
}

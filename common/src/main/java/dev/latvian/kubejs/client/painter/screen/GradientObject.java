package dev.latvian.kubejs.client.painter.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.math.Matrix4f;
import dev.latvian.kubejs.client.painter.PainterObjectProperties;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GradientObject extends ScreenPainterObject {
	private int colorTL = 0xFFFFFFFF;
	private int colorTR = 0xFFFFFFFF;
	private int colorBL = 0xFFFFFFFF;
	private int colorBR = 0xFFFFFFFF;
	private ResourceLocation texture = null;
	private float u0 = 0F;
	private float v0 = 0F;
	private float u1 = 1F;
	private float v1 = 1F;

	@Override
	protected void load(PainterObjectProperties properties) {
		super.load(properties);
		colorTL = properties.getARGB("colorTL", colorTL);
		colorTR = properties.getARGB("colorTR", colorTR);
		colorBL = properties.getARGB("colorBL", colorBL);
		colorBR = properties.getARGB("colorBR", colorBR);

		if (properties.hasAny("colorT")) {
			colorTL = colorTR = properties.getARGB("colorT", 0xFFFFFFFF);
		}

		if (properties.hasAny("colorB")) {
			colorBL = colorBR = properties.getARGB("colorB", 0xFFFFFFFF);
		}

		if (properties.hasAny("colorL")) {
			colorTL = colorBL = properties.getARGB("colorL", 0xFFFFFFFF);
		}

		if (properties.hasAny("colorR")) {
			colorTR = colorBR = properties.getARGB("colorR", 0xFFFFFFFF);
		}

		texture = properties.getResourceLocation("texture", texture);
		u0 = properties.getFloat("u0", u0);
		v0 = properties.getFloat("v0", v0);
		u1 = properties.getFloat("u1", u1);
		v1 = properties.getFloat("v1", v1);
	}

	@Override
	public void draw(ScreenPaintEventJS event) {
		float ax = event.alignX(x, w, alignX);
		float ay = event.alignY(y, h, alignY);
		Matrix4f m = event.getMatrix();

		event.setSmoothShade(true);
		RenderSystem.alphaFunc(GL11.GL_GREATER, 0.001F);

		if (texture == null) {
			event.setTextureEnabled(false);
			event.beginQuads(DefaultVertexFormat.POSITION_COLOR);
			event.vertex(m, ax, ay + h, z, colorBL);
			event.vertex(m, ax + w, ay + h, z, colorBR);
			event.vertex(m, ax + w, ay, z, colorTR);
			event.vertex(m, ax, ay, z, colorTL);
			event.end();
			event.setTextureEnabled(true);
		} else {
			event.bindTexture(texture);
			event.beginQuads(DefaultVertexFormat.POSITION_COLOR_TEX);
			event.vertex(m, ax, ay + h, z, colorBL, u0, v1);
			event.vertex(m, ax + w, ay + h, z, colorBR, u1, v1);
			event.vertex(m, ax + w, ay, z, colorTR, u1, v0);
			event.vertex(m, ax, ay, z, colorTL, u0, v0);
			event.end();
		}

		event.setSmoothShade(false);
		RenderSystem.defaultAlphaFunc();
	}
}

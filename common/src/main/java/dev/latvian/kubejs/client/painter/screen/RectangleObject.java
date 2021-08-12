package dev.latvian.kubejs.client.painter.screen;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import dev.latvian.kubejs.client.painter.PainterObjectProperties;
import net.minecraft.resources.ResourceLocation;

public class RectangleObject extends ScreenPainterObject {
	private int color = 0xFFFFFFFF;
	private ResourceLocation texture = null;
	private float u0 = 0F;
	private float v0 = 0F;
	private float u1 = 1F;
	private float v1 = 1F;

	@Override
	protected void load(PainterObjectProperties properties) {
		super.load(properties);

		color = properties.getARGB("color", color);
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

		if (texture == null) {
			event.setTextureEnabled(false);
			event.beginQuads(DefaultVertexFormat.POSITION_COLOR);
			event.rectangle(ax, ay, z, w, h, color);
			event.end();
			event.setTextureEnabled(true);
		} else {
			event.bindTexture(texture);
			event.beginQuads(DefaultVertexFormat.POSITION_COLOR_TEX);
			event.rectangle(ax, ay, z, w, h, color, u0, v0, u1, v1);
			event.end();
		}
	}
}

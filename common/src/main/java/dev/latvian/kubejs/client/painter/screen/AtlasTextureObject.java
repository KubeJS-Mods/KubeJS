package dev.latvian.kubejs.client.painter.screen;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import dev.latvian.kubejs.client.painter.PainterObjectProperties;
import dev.latvian.kubejs.util.ColorKJS;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public class AtlasTextureObject extends ScreenPainterObject {
	private int color = 0xFFFFFFFF;
	private ResourceLocation texture = null;

	public AtlasTextureObject color(ColorKJS col) {
		color = col.getArgbKJS();
		return this;
	}

	@Override
	protected void load(PainterObjectProperties properties) {
		super.load(properties);

		color = properties.getARGB("color", color);
		texture = properties.getResourceLocation("texture", texture);
	}

	@Override
	public void draw(ScreenPaintEventJS event) {
		if (texture == null) {
			return;
		}

		TextureAtlasSprite sprite = event.getTextureAtlas().getSprite(texture);

		float u0 = sprite.getU0();
		float v0 = sprite.getV0();
		float u1 = sprite.getU1();
		float v1 = sprite.getV1();
		event.bindTexture(TextureAtlas.LOCATION_BLOCKS);
		event.beginQuads(DefaultVertexFormat.POSITION_COLOR_TEX);
		event.rectangle(x, y, z, w, h, color, u0, v0, u1, v1);
		event.end();
	}
}

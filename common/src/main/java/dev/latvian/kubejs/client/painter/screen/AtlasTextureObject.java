package dev.latvian.kubejs.client.painter.screen;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import dev.latvian.kubejs.client.painter.PainterObjectProperties;
import dev.latvian.mods.rhino.util.unit.Unit;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public class AtlasTextureObject extends ScreenPainterObject {
	private Unit color = PainterObjectProperties.WHITE_COLOR;
	private ResourceLocation texture = null;

	@Override
	protected void load(PainterObjectProperties properties) {
		super.load(properties);

		color = properties.getColor("color", color);
		texture = properties.getResourceLocation("texture", texture);
	}

	@Override
	public void draw(ScreenPaintEventJS event) {
		if (texture == null) {
			return;
		}

		float aw = w.get();
		float ah = h.get();
		float ax = event.alignX(x.get(), aw, alignX);
		float ay = event.alignY(y.get(), ah, alignY);
		float az = z.get();

		TextureAtlasSprite sprite = event.getTextureAtlas().getSprite(texture);

		float u0 = sprite.getU0();
		float v0 = sprite.getV0();
		float u1 = sprite.getU1();
		float v1 = sprite.getV1();
		event.bindTexture(TextureAtlas.LOCATION_BLOCKS);
		event.beginQuads(DefaultVertexFormat.POSITION_COLOR_TEX);
		event.rectangle(ax, ay, az, aw, ah, color.getAsInt(), u0, v0, u1, v1);
		event.end();
	}
}

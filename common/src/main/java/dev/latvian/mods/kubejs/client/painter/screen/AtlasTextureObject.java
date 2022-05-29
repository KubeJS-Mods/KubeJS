package dev.latvian.mods.kubejs.client.painter.screen;

import dev.latvian.mods.kubejs.client.painter.PainterObjectProperties;
import dev.latvian.mods.unit.FixedColorUnit;
import dev.latvian.mods.unit.Unit;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

public class AtlasTextureObject extends ScreenPainterObject {
	private Unit color = FixedColorUnit.WHITE;
	private ResourceLocation atlas = InventoryMenu.BLOCK_ATLAS;
	private ResourceLocation texture = null;
	private TextureAtlas textureAtlas;

	@Override
	protected void load(PainterObjectProperties properties) {
		super.load(properties);

		color = properties.getColor("color", color);
		atlas = properties.getResourceLocation("atlas", atlas);
		texture = properties.getResourceLocation("texture", texture);
		textureAtlas = null;
	}

	@Override
	public void draw(ScreenPaintEventJS event) {
		if (texture == null) {
			return;
		}

		if (textureAtlas == null) {
			textureAtlas = event.mc.getModelManager().getAtlas(atlas);
		}

		if (textureAtlas == null) {
			return;
		}

		var aw = w.getFloat(event);
		var ah = h.getFloat(event);
		var ax = event.alignX(x.getFloat(event), aw, alignX);
		var ay = event.alignY(y.getFloat(event), ah, alignY);
		var az = z.getFloat(event);

		var sprite = textureAtlas.getSprite(texture);

		// Happens when painter is used server side and then trigger client reload
		if(sprite != null) {
			var u0 = sprite.getU0();
			var v0 = sprite.getV0();
			var u1 = sprite.getU1();
			var v1 = sprite.getV1();
			event.resetShaderColor();
			event.setPositionColorTextureShader();
			event.setShaderTexture(atlas);
			event.beginQuads(true);
			event.rectangle(ax, ay, az, aw, ah, color.getInt(event), u0, v0, u1, v1);
			event.end();
		}
	}
}

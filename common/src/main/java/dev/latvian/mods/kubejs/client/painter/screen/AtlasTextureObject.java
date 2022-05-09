package dev.latvian.mods.kubejs.client.painter.screen;

import dev.latvian.mods.kubejs.client.painter.PainterObjectProperties;
import dev.latvian.mods.rhino.util.unit.Unit;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

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

		var aw = w.get();
		var ah = h.get();
		var ax = event.alignX(x.get(), aw, alignX);
		var ay = event.alignY(y.get(), ah, alignY);
		var az = z.get();

		var sprite = event.getTextureAtlas().getSprite(texture);

		var u0 = sprite.getU0();
		var v0 = sprite.getV0();
		var u1 = sprite.getU1();
		var v1 = sprite.getV1();
		event.setPositionTextureColorShader();
		event.setTexture(InventoryMenu.BLOCK_ATLAS);
		event.beginQuads(true);
		event.rectangle(ax, ay, az, aw, ah, color.getAsInt(), u0, v0, u1, v1);
		event.end();
	}
}

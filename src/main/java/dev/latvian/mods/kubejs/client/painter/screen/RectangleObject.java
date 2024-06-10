package dev.latvian.mods.kubejs.client.painter.screen;

import dev.latvian.mods.kubejs.client.painter.Painter;
import dev.latvian.mods.kubejs.client.painter.PainterObjectProperties;
import dev.latvian.mods.unit.FixedColorUnit;
import dev.latvian.mods.unit.FixedNumberUnit;
import dev.latvian.mods.unit.Unit;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;

public class RectangleObject extends BoxObject {
	public Unit color = FixedColorUnit.WHITE;
	public ResourceLocation texture = null;
	public Unit u0 = FixedNumberUnit.ZERO;
	public Unit v0 = FixedNumberUnit.ZERO;
	public Unit u1 = FixedNumberUnit.ONE;
	public Unit v1 = FixedNumberUnit.ONE;

	public RectangleObject(Painter painter) {
		super(painter);
	}

	@Override
	protected void load(HolderLookup.Provider registries, PainterObjectProperties properties) {
		super.load(registries, properties);

		color = properties.getColor("color", color);
		texture = properties.getResourceLocation("texture", texture);
		u0 = properties.getUnit("u0", u0);
		v0 = properties.getUnit("v0", v0);
		u1 = properties.getUnit("u1", u1);
		v1 = properties.getUnit("v1", v1);
	}

	@Override
	public void draw(PaintScreenKubeEvent event) {
		var aw = w.getFloat(event);
		var ah = h.getFloat(event);
		var ax = event.alignX(x.getFloat(event), aw, alignX);
		var ay = event.alignY(y.getFloat(event), ah, alignY);
		var az = z.getFloat(event);

		if (texture == null) {
			event.setPositionColorShader();
			event.blend(true);
			event.beginQuads(false);
			event.rectangle(ax, ay, az, aw, ah, color.getInt(event));
			event.end();
		} else {
			float u0f = u0.getFloat(event);
			float v0f = v0.getFloat(event);
			float u1f = u1.getFloat(event);
			float v1f = v1.getFloat(event);

			event.setPositionColorTextureShader();
			event.setShaderTexture(texture);
			event.blend(true);
			event.beginQuads(true);
			event.rectangle(ax, ay, az, aw, ah, color.getInt(event), u0f, v0f, u1f, v1f);
			event.end();
		}
	}
}

package dev.latvian.mods.kubejs.client.painter.screen;

import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.latvian.mods.kubejs.client.painter.Painter;
import dev.latvian.mods.kubejs.client.painter.PainterObjectProperties;
import dev.latvian.mods.unit.FixedColorUnit;
import dev.latvian.mods.unit.FixedNumberUnit;
import dev.latvian.mods.unit.Unit;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;

public class GradientObject extends BoxObject {
	public Unit colorTL = FixedColorUnit.WHITE;
	public Unit colorTR = FixedColorUnit.WHITE;
	public Unit colorBL = FixedColorUnit.WHITE;
	public Unit colorBR = FixedColorUnit.WHITE;
	public ResourceLocation texture = null;
	public Unit u0 = FixedNumberUnit.ZERO;
	public Unit v0 = FixedNumberUnit.ZERO;
	public Unit u1 = FixedNumberUnit.ONE;
	public Unit v1 = FixedNumberUnit.ONE;

	public GradientObject(Painter painter) {
		super(painter);
	}

	@Override
	protected void load(HolderLookup.Provider registries, PainterObjectProperties properties) {
		super.load(registries, properties);
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
	public void draw(PaintScreenKubeEvent event) {
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
			event.blend(true);
			var buf = event.tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
			buf.addVertex(m, ax + aw, ay, az).setColor(colTR);
			buf.addVertex(m, ax, ay, az).setColor(colTL);
			buf.addVertex(m, ax, ay + ah, az).setColor(colBL);
			buf.addVertex(m, ax + aw, ay + ah, az).setColor(colBR);
			BufferUploader.drawWithShader(buf.buildOrThrow());
		} else {
			float u0f = u0.getFloat(event);
			float v0f = v0.getFloat(event);
			float u1f = u1.getFloat(event);
			float v1f = v1.getFloat(event);

			event.setPositionTextureColorShader();
			event.setShaderTexture(texture);
			event.blend(true);
			var buf = event.tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
			buf.addVertex(m, ax + aw, ay, az).setUv(u1f, v0f).setColor(colTR);
			buf.addVertex(m, ax, ay, az).setUv(u0f, v0f).setColor(colTL);
			buf.addVertex(m, ax, ay + ah, az).setUv(u0f, v1f).setColor(colBL);
			buf.addVertex(m, ax + aw, ay + ah, az).setUv(u1f, v1f).setColor(colBR);
			BufferUploader.drawWithShader(buf.buildOrThrow());
		}
	}
}

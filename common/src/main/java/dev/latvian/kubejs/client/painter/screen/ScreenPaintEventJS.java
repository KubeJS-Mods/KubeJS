package dev.latvian.kubejs.client.painter.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import dev.latvian.kubejs.client.painter.PaintEventJS;
import dev.latvian.kubejs.client.painter.Painter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class ScreenPaintEventJS extends PaintEventJS {
	public final int mouseX;
	public final int mouseY;
	public final int width;
	public final int height;
	public final boolean inventory;

	public ScreenPaintEventJS(Minecraft m, Screen s, PoseStack ps, int mx, int my, float d) {
		super(m, ps, d, s);
		mouseX = mx;
		mouseY = my;
		width = mc.getWindow().getGuiScaledWidth();
		height = mc.getWindow().getGuiScaledHeight();
		inventory = true;
	}

	public ScreenPaintEventJS(Minecraft m, PoseStack ps, float d) {
		super(m, ps, d, null);
		mouseX = -1;
		mouseY = -1;
		width = mc.getWindow().getGuiScaledWidth();
		height = mc.getWindow().getGuiScaledHeight();
		inventory = false;
	}

	public float alignX(float x, float w, int alignX) {
		switch (alignX) {
			case Painter.RIGHT:
				return width - w + x;
			case Painter.CENTER:
				return (width - w) / 2 + x;
			default:
				return x;
		}
	}

	public float alignY(float y, float h, int alignY) {
		switch (alignY) {
			case Painter.BOTTOM:
				return height - h + y;
			case Painter.CENTER:
				return (height - h) / 2 + y;
			default:
				return y;
		}
	}

	public void translate(double x, double y) {
		translate(x, y, 0D);
	}

	public void scale(float x, float y) {
		scale(x, y, 1F);
	}

	public void scale(float scale) {
		scale(scale, scale, 1F);
	}

	public void rotateDeg(float angle) {
		matrices.mulPose(Vector3f.ZP.rotationDegrees(angle));
	}

	public void rotateRad(float angle) {
		matrices.mulPose(Vector3f.ZP.rotation(angle));
	}

	public void rectangle(float x, float y, float z, float w, float h, int color) {
		Matrix4f m = getMatrix();
		vertex(m, x, y + h, z, color);
		vertex(m, x + w, y + h, z, color);
		vertex(m, x + w, y, z, color);
		vertex(m, x, y, z, color);
	}

	public void rectangle(float x, float y, float z, float w, float h, int color, float u0, float v0, float u1, float v1) {
		Matrix4f m = getMatrix();
		vertex(m, x, y + h, z, color, u0, v1);
		vertex(m, x + w, y + h, z, color, u1, v1);
		vertex(m, x + w, y, z, color, u1, v0);
		vertex(m, x, y, z, color, u0, v0);
	}

	public void text(Component text, float x, float y, int color, boolean shadow) {
		rawText(text.getVisualOrderText(), x, y, color, shadow);
	}

	public void rawText(FormattedCharSequence text, float x, float y, int color, boolean shadow) {
		if (shadow) {
			font.drawShadow(matrices, text, x, y, color);
		} else {
			font.draw(matrices, text, x, y, color);
		}
	}
}

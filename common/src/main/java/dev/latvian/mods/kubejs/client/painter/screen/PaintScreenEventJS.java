package dev.latvian.mods.kubejs.client.painter.screen;

import com.mojang.math.Axis;
import dev.latvian.mods.kubejs.client.painter.PaintEventJS;
import dev.latvian.mods.kubejs.client.painter.Painter;
import dev.latvian.mods.unit.UnitVariables;
import dev.latvian.mods.unit.VariableSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class PaintScreenEventJS extends PaintEventJS implements UnitVariables {
	public final int mouseX;
	public final int mouseY;
	public final int width;
	public final int height;
	public final boolean inventory;

	public PaintScreenEventJS(Minecraft m, Screen s, GuiGraphics graphics, int mx, int my, float d) {
		super(m, graphics, d, s);
		mouseX = mx;
		mouseY = my;
		width = mc.getWindow().getGuiScaledWidth();
		height = mc.getWindow().getGuiScaledHeight();
		inventory = true;
	}

	public PaintScreenEventJS(Minecraft m, GuiGraphics graphics, float d) {
		super(m, graphics, d, null);
		mouseX = -1;
		mouseY = -1;
		width = mc.getWindow().getGuiScaledWidth();
		height = mc.getWindow().getGuiScaledHeight();
		inventory = false;
	}

	public float alignX(float x, float w, int alignX) {
		return switch (alignX) {
			case Painter.RIGHT -> width - w + x;
			case Painter.CENTER -> (width - w) / 2 + x;
			default -> x;
		};
	}

	public float alignY(float y, float h, int alignY) {
		return switch (alignY) {
			case Painter.BOTTOM -> height - h + y;
			case Painter.CENTER -> (height - h) / 2 + y;
			default -> y;
		};
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
		matrices.mulPose(Axis.ZP.rotationDegrees(angle));
	}

	public void rotateRad(float angle) {
		matrices.mulPose(Axis.ZP.rotation(angle));
	}

	public void rectangle(float x, float y, float z, float w, float h, int color) {
		var m = getMatrix();
		vertex(m, x + w, y, z, color);
		vertex(m, x, y, z, color);
		vertex(m, x, y + h, z, color);
		vertex(m, x + w, y + h, z, color);
	}

	public void rectangle(float x, float y, float z, float w, float h, int color, float u0, float v0, float u1, float v1) {
		var m = getMatrix();
		vertex(m, x + w, y, z, color, u1, v0);
		vertex(m, x, y, z, color, u0, v0);
		vertex(m, x, y + h, z, color, u0, v1);
		vertex(m, x + w, y + h, z, color, u1, v1);
	}

	public void text(Component text, int x, int y, int color, boolean shadow) {
		rawText(text.getVisualOrderText(), x, y, color, shadow);
	}

	public void rawText(FormattedCharSequence text, int x, int y, int color, boolean shadow) {
		graphics.drawString(mc.font, text, x, y, color, shadow);
	}

	@Override
	public VariableSet getVariables() {
		return Painter.INSTANCE.getVariables();
	}
}

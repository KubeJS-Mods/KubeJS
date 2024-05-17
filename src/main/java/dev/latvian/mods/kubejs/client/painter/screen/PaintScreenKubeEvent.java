package dev.latvian.mods.kubejs.client.painter.screen;

import com.mojang.math.Axis;
import dev.latvian.mods.kubejs.client.painter.PaintKubeEvent;
import dev.latvian.mods.kubejs.client.painter.Painter;
import dev.latvian.mods.unit.UnitVariables;
import dev.latvian.mods.unit.VariableSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class PaintScreenKubeEvent extends PaintKubeEvent implements UnitVariables {
	public final Painter painter;
	public final int mouseX;
	public final int mouseY;
	public final int width;
	public final int height;
	public final boolean inventory;

	public PaintScreenKubeEvent(Minecraft m, Screen s, GuiGraphics graphics, Painter painter, int mx, int my, float d) {
		super(m, graphics, d, s);
		this.painter = painter;
		this.mouseX = mx;
		this.mouseY = my;
		this.width = mc.getWindow().getGuiScaledWidth();
		this.height = mc.getWindow().getGuiScaledHeight();
		this.inventory = true;
	}

	public PaintScreenKubeEvent(Minecraft m, GuiGraphics graphics, Painter painter, float d) {
		super(m, graphics, d, null);
		this.painter = painter;
		this.mouseX = -1;
		this.mouseY = -1;
		this.width = mc.getWindow().getGuiScaledWidth();
		this.height = mc.getWindow().getGuiScaledHeight();
		this.inventory = false;
	}

	@Override
	public VariableSet getVariables() {
		return painter.getVariables();
	}

	public float alignX(float x, float w, AlignMode alignX) {
		return switch (alignX) {
			case END -> width - w + x;
			case CENTER -> (width - w) / 2 + x;
			default -> x;
		};
	}

	public float alignY(float y, float h, AlignMode alignY) {
		return switch (alignY) {
			case END -> height - h + y;
			case CENTER -> (height - h) / 2 + y;
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
}

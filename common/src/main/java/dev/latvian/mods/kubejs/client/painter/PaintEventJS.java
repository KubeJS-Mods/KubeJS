package dev.latvian.mods.kubejs.client.painter;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.latvian.mods.kubejs.client.ClientEventJS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.function.Supplier;

public class PaintEventJS extends ClientEventJS {
	public final Minecraft mc;
	public final Font font;
	public final GuiGraphics graphics;
	public final PoseStack matrices;
	public final Tesselator tesselator;
	public final BufferBuilder buffer;
	public final float delta;
	public final Screen screen;

	public PaintEventJS(Minecraft m, GuiGraphics g, float d, @Nullable Screen s) {
		mc = m;
		font = mc.font;
		graphics = g;
		matrices = g.pose();
		tesselator = Tesselator.getInstance();
		buffer = tesselator.getBuilder();
		delta = d;
		screen = s;
	}

	public void push() {
		matrices.pushPose();
	}

	public void pop() {
		matrices.popPose();
	}

	public void translate(double x, double y, double z) {
		matrices.translate(x, y, z);
	}

	public void scale(float x, float y, float z) {
		matrices.scale(x, y, z);
	}

	public void multiply(Quaternionf q) {
		matrices.mulPose(q);
	}

	public void multiplyWithMatrix(Matrix4f m) {
		matrices.mulPoseMatrix(m);
	}

	public Matrix4f getMatrix() {
		return matrices.last().pose();
	}

	public void bindTextureForSetup(ResourceLocation tex) {
		mc.getTextureManager().bindForSetup(tex);
	}

	public void setShaderColor(float r, float g, float b, float a) {
		RenderSystem.setShaderColor(r, g, b, a);
	}

	public void resetShaderColor() {
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
	}

	public void setShaderTexture(ResourceLocation tex) {
		RenderSystem.setShaderTexture(0, tex);
	}

	public void begin(VertexFormat.Mode type, VertexFormat format) {
		buffer.begin(type, format);
	}

	public void beginQuads(VertexFormat format) {
		begin(VertexFormat.Mode.QUADS, format);
	}

	public void beginQuads(boolean texture) {
		beginQuads(texture ? DefaultVertexFormat.POSITION_COLOR_TEX : DefaultVertexFormat.POSITION_COLOR);
	}

	public void vertex(Matrix4f m, float x, float y, float z, int col) {
		buffer.vertex(m, x, y, z).color((col >> 16) & 0xFF, (col >> 8) & 0xFF, col & 0xFF, (col >> 24) & 0xFF).endVertex();
	}

	public void vertex(Matrix4f m, float x, float y, float z, int col, float u, float v) {
		buffer.vertex(m, x, y, z).color((col >> 16) & 0xFF, (col >> 8) & 0xFF, col & 0xFF, (col >> 24) & 0xFF).uv(u, v).endVertex();
	}

	public void end() {
		tesselator.end();
	}

	public void setShaderInstance(Supplier<ShaderInstance> shader) {
		RenderSystem.setShader(shader);
	}

	public void setPositionColorShader() {
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
	}

	public void setPositionColorTextureShader() {
		RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
	}
}

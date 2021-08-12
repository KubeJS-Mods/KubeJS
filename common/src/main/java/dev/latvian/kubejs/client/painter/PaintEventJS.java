package dev.latvian.kubejs.client.painter;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import dev.latvian.kubejs.client.ClientEventJS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

public class PaintEventJS extends ClientEventJS {
	public final Minecraft mc;
	public final Font font;
	public final PoseStack matrices;
	public final Tesselator tesselator;
	public final BufferBuilder buffer;
	public final float delta;
	public final Screen screen;
	private TextureAtlas textureAtlas;

	public PaintEventJS(Minecraft m, PoseStack p, float d, @Nullable Screen s) {
		mc = m;
		font = mc.font;
		matrices = p;
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

	public Matrix4f getMatrix() {
		return matrices.last().pose();
	}

	public void bindTexture(ResourceLocation tex) {
		mc.getTextureManager().bind(tex);
	}

	public void begin(int type, VertexFormat format) {
		buffer.begin(type, format);
	}

	public void beginQuads(VertexFormat format) {
		begin(GL11.GL_QUADS, format);
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

	public TextureAtlas getTextureAtlas() {
		if (textureAtlas == null) {
			textureAtlas = mc.getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS);
		}

		return textureAtlas;
	}

	public void setSmoothShade(boolean smooth) {
		RenderSystem.shadeModel(smooth ? GL11.GL_SMOOTH : GL11.GL_FLAT);
	}

	public void setTextureEnabled(boolean enabled) {
		if (enabled) {
			RenderSystem.enableTexture();
		} else {
			RenderSystem.disableTexture();
		}
	}
}

package dev.latvian.mods.kubejs.client.painter;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import dev.latvian.mods.kubejs.client.ClientKubeEvent;
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

public class PaintKubeEvent extends ClientKubeEvent {
	public final Minecraft mc;
	public final Font font;
	public final GuiGraphics graphics;
	public final PoseStack matrices;
	public final Tesselator tesselator;
	public final float delta;
	public final Screen screen;

	public PaintKubeEvent(Minecraft m, GuiGraphics g, float d, @Nullable Screen s) {
		super(m.player);
		mc = m;
		font = mc.font;
		graphics = g;
		matrices = g.pose();
		tesselator = Tesselator.getInstance();
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
		matrices.mulPose(m);
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

	public void setShaderInstance(Supplier<ShaderInstance> shader) {
		RenderSystem.setShader(shader);
	}

	public void setPositionColorShader() {
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
	}

	public void setPositionTextureColorShader() {
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
	}

	public void blend(boolean enabled) {
		if (enabled) {
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
		} else {
			RenderSystem.disableBlend();
		}
	}
}

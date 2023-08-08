package dev.latvian.mods.kubejs.client.painter.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.latvian.mods.kubejs.client.painter.Painter;
import dev.latvian.mods.kubejs.client.painter.PainterObjectProperties;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.unit.FixedBooleanUnit;
import dev.latvian.mods.unit.FixedNumberUnit;
import dev.latvian.mods.unit.Unit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemObject extends ScreenPainterObject {
	private ItemStack itemStack = ItemStack.EMPTY;
	private Unit overlay = FixedBooleanUnit.TRUE;
	private String customText = "";
	private Unit rotation = FixedNumberUnit.ZERO;

	public ItemObject(Painter painter) {
		z = FixedNumberUnit.of(100);
	}

	@Override
	protected void load(PainterObjectProperties properties) {
		super.load(properties);

		if (properties.hasAny("item")) {
			itemStack = ItemStackJS.of(properties.tag.get("item"));
		}

		overlay = properties.getUnit("overlay", overlay);
		customText = properties.getString("customText", customText);
		rotation = properties.getUnit("rotation", rotation);
	}

	@Override
	public void draw(PaintScreenEventJS event) {
		if (itemStack.isEmpty()) {
			return;
		}

		var aw = w.getFloat(event);
		var ah = h.getFloat(event);
		var ax = event.alignX(x.getFloat(event), aw, alignX);
		var ay = event.alignY(y.getFloat(event), ah, alignY);
		var az = z.getFloat(event);

		event.push();
		event.translate(ax, ay, az);

		if (rotation != FixedNumberUnit.ZERO) {
			event.rotateRad(rotation.getFloat(event));
		}

		event.scale(aw / 16F, ah / 16F, 1F);
		drawItem(event.matrices, itemStack, 0, overlay.getBoolean(event), customText.isEmpty() ? null : customText);
		event.pop();
	}

	public static void drawItem(PoseStack poseStack, ItemStack stack, int hash, boolean renderOverlay, @Nullable String text) {
		if (stack.isEmpty()) {
			return;
		}

		var mc = Minecraft.getInstance();
		var itemRenderer = mc.getItemRenderer();
		var bakedModel = itemRenderer.getModel(stack, null, mc.player, hash);

		Minecraft.getInstance().getTextureManager().getTexture(InventoryMenu.BLOCK_ATLAS).setFilter(false, false);
		RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		PoseStack modelViewStack = RenderSystem.getModelViewStack();
		modelViewStack.pushPose();
		modelViewStack.mulPoseMatrix(poseStack.last().pose());
		// modelViewStack.translate(x, y, 100.0D + this.blitOffset);
		modelViewStack.scale(1F, -1F, 1F);
		modelViewStack.scale(16F, 16F, 16F);
		RenderSystem.applyModelViewMatrix();
		MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
		var flatLight = !bakedModel.usesBlockLight();

		if (flatLight) {
			Lighting.setupForFlatItems();
		}

		itemRenderer.render(stack, ItemDisplayContext.GUI, false, new PoseStack(), bufferSource, 0xF000F0, OverlayTexture.NO_OVERLAY, bakedModel);
		bufferSource.endBatch();
		RenderSystem.enableDepthTest();

		if (flatLight) {
			Lighting.setupFor3DItems();
		}

		modelViewStack.popPose();
		RenderSystem.applyModelViewMatrix();

		if (renderOverlay) {
			var t = Tesselator.getInstance();
			var font = mc.font;

			if (stack.getCount() != 1 || text != null) {
				var s = text == null ? String.valueOf(stack.getCount()) : text;
				poseStack.pushPose();
				poseStack.translate(9D - font.width(s), 1D, 20D);
				font.drawInBatch(s, 0F, 0F, 0xFFFFFF, true, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, 0xF000F0);
				bufferSource.endBatch();
				poseStack.popPose();
			}

			if (stack.isBarVisible()) {
				RenderSystem.disableDepthTest();
				RenderSystem.disableBlend();
				var barWidth = stack.getBarWidth();
				var barColor = stack.getBarColor();
				draw(poseStack, t, -6, 5, 13, 2, 0, 0, 0, 255);
				draw(poseStack, t, -6, 5, barWidth, 1, barColor >> 16 & 255, barColor >> 8 & 255, barColor & 255, 255);
				RenderSystem.enableBlend();
				RenderSystem.enableDepthTest();
			}

			var cooldown = mc.player == null ? 0F : mc.player.getCooldowns().getCooldownPercent(stack.getItem(), mc.getFrameTime());

			if (cooldown > 0F) {
				RenderSystem.disableDepthTest();
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				draw(poseStack, t, -8, Mth.floor(16F * (1F - cooldown)) - 8, 16, Mth.ceil(16F * cooldown), 255, 255, 255, 127);
				RenderSystem.enableDepthTest();
			}
		}
	}

	private static void draw(PoseStack matrixStack, Tesselator t, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
		if (width <= 0 || height <= 0) {
			return;
		}

		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		var m = matrixStack.last().pose();
		var renderer = t.getBuilder();
		renderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		renderer.vertex(m, x, y, 0).color(red, green, blue, alpha).endVertex();
		renderer.vertex(m, x, y + height, 0).color(red, green, blue, alpha).endVertex();
		renderer.vertex(m, x + width, y + height, 0).color(red, green, blue, alpha).endVertex();
		renderer.vertex(m, x + width, y, 0).color(red, green, blue, alpha).endVertex();
		t.end();
	}
}

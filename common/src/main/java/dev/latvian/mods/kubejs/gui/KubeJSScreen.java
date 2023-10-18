package dev.latvian.mods.kubejs.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class KubeJSScreen extends AbstractContainerScreen<KubeJSMenu> implements MenuAccess<KubeJSMenu> {
	private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation("textures/gui/container/generic_54.png");
	public final int containerRows;

	public KubeJSScreen(KubeJSMenu menu, Inventory inventory, Component component) {
		super(menu, inventory, component);
		this.imageWidth = menu.guiData.width;
		this.imageHeight = menu.guiData.height;
		this.containerRows = (menu.guiData.inventory.kjs$getSlots() + 8) / 9;
		this.imageHeight = 114 + this.containerRows * 18;
		this.inventoryLabelY = this.imageHeight - 94;
	}

	@Override
	public void render(PoseStack guiGraphics, int i, int j, float f) {
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, i, j, f);
		this.renderTooltip(guiGraphics, i, j);
	}

	@Override
	protected void renderBg(PoseStack guiGraphics, float f, int i, int j) {
		int k = (this.width - this.imageWidth) / 2;
		int l = (this.height - this.imageHeight) / 2;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		RenderSystem.setShaderTexture(0, CONTAINER_BACKGROUND);
		blit(guiGraphics, k, l, 0, 0, this.imageWidth, this.containerRows * 18 + 17);
		blit(guiGraphics, k, l + this.containerRows * 18 + 17, 0, 126, this.imageWidth, 96);
	}
}

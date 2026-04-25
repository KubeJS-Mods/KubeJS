package dev.latvian.mods.kubejs.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class KubeJSScreen extends AbstractContainerScreen<KubeJSMenu> implements MenuAccess<KubeJSMenu> {
	private static final Identifier CONTAINER_BACKGROUND = Identifier.parse("kubejs:textures/gui/container/generic_background.png");

	public final int containerRows;
	public final int containerColumns;
	private final int xOffset;

	public KubeJSScreen(KubeJSMenu menu, Inventory inventory, Component component) {
		super(menu, inventory, component, 176, 114 + menu.guiData.inventoryHeight * 18);
		this.containerRows = menu.guiData.inventoryHeight;
		this.containerColumns = menu.guiData.inventoryWidth;
		this.inventoryLabelY = this.imageHeight - 94;
		this.xOffset = 88 - 9 * this.containerColumns;

	}


	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int i, int j, float f) {
		this.extractBackground(graphics, i, j, f);
		super.extractRenderState(graphics, i, j, f);
		this.extractTooltip(graphics, i, j);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int i, int j, float f) {
		super.extractBackground(graphics, i, j, f);

		int k = (this.width - this.imageWidth) / 2;
		int l = (this.height - this.imageHeight) / 2;
		graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_BACKGROUND, k, l, 0, 0, this.imageWidth, this.containerRows * 18 + 17, 256, 256);
		graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_BACKGROUND, k, l + this.containerRows * 18 + 17, 0, 126, this.imageWidth, 96, 256, 256);

		for (int slotY = 0; slotY < this.containerRows; slotY++) {
			graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_BACKGROUND, k + this.xOffset, l + 17 + slotY * 18, 7, 139, 18 * containerColumns, 18, 256, 256);
		}
	}
}

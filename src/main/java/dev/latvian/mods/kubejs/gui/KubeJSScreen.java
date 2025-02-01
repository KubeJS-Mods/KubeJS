package dev.latvian.mods.kubejs.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class KubeJSScreen extends AbstractContainerScreen<KubeJSMenu> implements MenuAccess<KubeJSMenu> {
	private static final ResourceLocation CONTAINER_BACKGROUND = ResourceLocation.parse("kubejs:textures/gui/container/generic_background.png");

	public final int containerRows;
	public final int containerColumns;
	private final int xOffset;

	public KubeJSScreen(KubeJSMenu menu, Inventory inventory, Component component) {
		super(menu, inventory, component);
		this.containerRows = menu.guiData.inventoryHeight;
		this.containerColumns = menu.guiData.inventoryWidth;
		this.imageHeight = 114 + this.containerRows * 18;
		this.inventoryLabelY = this.imageHeight - 94;
		this.xOffset = 88 - 9 * this.containerColumns;

	}

	@Override
	public void render(GuiGraphics guiGraphics, int i, int j, float f) {
		this.renderBackground(guiGraphics, i, j, f);
		super.render(guiGraphics, i, j, f);
		this.renderTooltip(guiGraphics, i, j);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float f, int i, int j) {
		int k = (this.width - this.imageWidth) / 2;
		int l = (this.height - this.imageHeight) / 2;
		guiGraphics.blit(CONTAINER_BACKGROUND, k, l, 0, 0, this.imageWidth, this.containerRows * 18 + 17);
		guiGraphics.blit(CONTAINER_BACKGROUND, k, l + this.containerRows * 18 + 17, 0, 126, this.imageWidth, 96);

		for (int slotY = 0; slotY < this.containerRows; slotY++) {
			guiGraphics.blit(CONTAINER_BACKGROUND, k + this.xOffset , l + 17 + slotY * 18, 7, 139, 18 * containerColumns, 18);
		}

	}
}

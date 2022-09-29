package dev.latvian.mods.kubejs.block.entity.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

class BlockEntityMenu extends AbstractContainerMenu {
	protected BlockEntityMenu(@Nullable MenuType<?> menuType, int i) {
		super(menuType, i);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int i) {
		return null;
	}

	@Override
	public boolean stillValid(Player player) {
		return false;
	}
}
public class BlockEntityScreen extends AbstractContainerScreen<BlockEntityMenu> {
	public BlockEntityScreen(BlockEntityMenu abstractContainerMenu, Inventory inventory, Component component) {
		super(abstractContainerMenu, inventory, component);
	}

	@Override
	protected void renderBg(PoseStack poseStack, float f, int i, int j) {

	}
}

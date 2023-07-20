package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.typings.JsInfo;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

@JsInfo("""
		Invoked when a player opens a chest.
				
		Same as `PlayerEvents.inventoryOpened`, but only for chests.
		""")
public class ChestEventJS extends InventoryEventJS {
	public ChestEventJS(Player player, AbstractContainerMenu menu) {
		super(player, menu);
	}

	@JsInfo("Gets the chest inventory.")
	public Container getInventory() {
		return ((ChestMenu) getInventoryContainer()).getContainer();
	}

	@Nullable
	@JsInfo("Gets the chest block.")
	public BlockContainerJS getBlock() {
		if (getInventory() instanceof BlockEntity be) {
			return getLevel().kjs$getBlock(be);
		}

		return null;
	}
}
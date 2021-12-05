package dev.latvian.mods.kubejs.item.fabric;

import dev.architectury.registry.block.ToolType;
import dev.latvian.mods.kubejs.player.PlayerJS;
import dev.latvian.mods.kubejs.world.BlockContainerJS;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemStackJSImpl {
	public static int _getHarvestLevel(ItemStack stack, ToolType tool, @Nullable PlayerJS<?> player, @Nullable BlockContainerJS block) {
		throw new UnsupportedOperationException("Getting harvest level of item is currently unsupported!");
	}
}

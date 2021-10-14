package dev.latvian.kubejs.item.fabric;

import dev.latvian.kubejs.player.PlayerJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import dev.architectury.architectury.registry.ToolType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemStackJSImpl {
	public static int _getHarvestLevel(ItemStack stack, ToolType tool, @Nullable PlayerJS<?> player, @Nullable BlockContainerJS block) {
		throw new UnsupportedOperationException("Getting harvest level of item is currently unsupported!");
	}
}

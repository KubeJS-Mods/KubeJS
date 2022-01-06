package dev.latvian.mods.kubejs.item.forge;

import dev.architectury.registry.block.ToolType;
import dev.latvian.mods.kubejs.level.world.BlockContainerJS;
import dev.latvian.mods.kubejs.player.PlayerJS;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class ItemStackJSImpl {
	public static int _getHarvestLevel(ItemStack stack, ToolType tool, @Nullable PlayerJS<?> player, @Nullable BlockContainerJS block) {
		// FIXME: return stack.getItem().getHarvestLevel(stack, net.minecraftforge.common.ToolType.get(tool.forgeName), player == null ? null : player.minecraftPlayer, block == null ? null : block.getBlockState());
		return -1;
	}
}

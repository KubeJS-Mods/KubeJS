package dev.latvian.kubejs.item.forge;

import dev.latvian.kubejs.player.PlayerJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import dev.architectury.registry.block.ToolType;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class ItemStackJSImpl {
	public static int _getHarvestLevel(ItemStack stack, ToolType tool, @Nullable PlayerJS<?> player, @Nullable BlockContainerJS block) {
		return stack.getItem().getHarvestLevel(stack, net.minecraftforge.common.ToolType.get(tool.forgeName), player == null ? null : player.minecraftPlayer, block == null ? null : block.getBlockState());
	}
}

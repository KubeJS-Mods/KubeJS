package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.block.BlockPickedKubeEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.extensions.IBlockStateExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IBlockStateExtension.class)
public interface IBlockStateExtensionMixin {
	@Shadow
	private BlockState self() {
		return null;
	}

	@Inject(method = "getCloneItemStack", at = @At("HEAD"), cancellable = true)
	private void kjs$getCloneItemStack(HitResult target, LevelReader levelReader, BlockPos pos, Player player, CallbackInfoReturnable<ItemStack> cir) {
		var override = BlockPickedKubeEvent.handle(self(), target, levelReader, pos, player);

		if (override != null) {
			cir.setReturnValue(override);
		}
	}
}

package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.block.BlockPickedKubeEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.extensions.IBlockStateExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IBlockStateExtension.class)
public interface IBlockStateExtensionMixin {
	@SuppressWarnings("DataFlowIssue")
	@Shadow
	private BlockState self() {
		return null;
	}

	@Inject(method = "getCloneItemStack", at = @At("HEAD"), cancellable = true)
	private void kjs$getCloneItemStack(BlockPos pos, LevelReader level, boolean includeData, Player player, CallbackInfoReturnable<ItemStack> cir) {
		BlockHitResult hit;
		if (level instanceof Level actualLevel) {
			double reach = player.blockInteractionRange();
			hit = actualLevel.clip(new ClipContext(
				player.getEyePosition(),
				player.getEyePosition().add(player.getLookAngle().scale(reach)),
				ClipContext.Block.OUTLINE,
				ClipContext.Fluid.NONE,
				player
			));
		} else {
			hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
		}

		var override = BlockPickedKubeEvent.handle(self(), hit, level, pos, player);
		if (override != null) {
			cir.setReturnValue(override);
		}
	}
}

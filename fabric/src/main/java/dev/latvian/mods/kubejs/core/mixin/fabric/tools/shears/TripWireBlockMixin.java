package dev.latvian.mods.kubejs.core.mixin.fabric.tools.shears;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.latvian.mods.kubejs.item.custom.ShearsItemBuilder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.TripWireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TripWireBlock.class)
public abstract class TripWireBlockMixin {
	@ModifyExpressionValue(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"), method = "playerWillDestroy")
	private boolean kjs$isShears(boolean original, @Local Player player) {
		return original || ShearsItemBuilder.isCustomShears(player.getMainHandItem());
	}
}
package dev.latvian.mods.kubejs.core.mixin.fabric.tools.shears;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.latvian.mods.kubejs.item.custom.ShearsItemBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.PumpkinBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({PumpkinBlock.class, BeehiveBlock.class})
public abstract class BlockInteractShearsMixin {
	@ModifyExpressionValue(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z", ordinal = 0), method = "use")
	private boolean kjs$isShears(boolean original, @Local ItemStack stack) {
		return original || ShearsItemBuilder.isCustomShears(stack);
	}
}
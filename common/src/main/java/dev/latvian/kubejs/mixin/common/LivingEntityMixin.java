package dev.latvian.kubejs.mixin.common;

import dev.latvian.kubejs.core.LivingEntityKJS;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author LatvianModder
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements LivingEntityKJS {
	@Inject(method = "eat", at = @At("HEAD"))
	private void foodEaten(Level world, ItemStack item, CallbackInfoReturnable<ItemStack> ci) {
		foodEatenKJS(item);
	}
}
package dev.latvian.mods.kubejs.core.mixin.fabric.tools.shears;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.latvian.mods.kubejs.item.custom.ShearsItemBuilder;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({Sheep.class, SnowGolem.class})
public abstract class EntityInteractShearsMixin {
	@ModifyExpressionValue(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"), method = "mobInteract")
	private boolean kjs$isShears(boolean original, @Local ItemStack stack) {
		return original || ShearsItemBuilder.isCustomShears(stack);
	}
}
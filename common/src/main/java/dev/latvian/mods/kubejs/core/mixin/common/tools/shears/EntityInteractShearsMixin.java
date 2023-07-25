package dev.latvian.mods.kubejs.core.mixin.common.tools.shears;

import dev.latvian.mods.kubejs.item.custom.ShearsItemBuilder;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({Sheep.class, SnowGolem.class})
public abstract class EntityInteractShearsMixin {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"), method = "mobInteract")
    private boolean isShears(ItemStack stack, Item item) {
        return ShearsItemBuilder.SHEARS_LIST.contains(stack.getItem()) || stack.is(item);
    }
}
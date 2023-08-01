package dev.latvian.mods.kubejs.core.mixin.fabric.tools.shears;

import dev.latvian.mods.kubejs.item.custom.ShearsItemBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.TripWireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TripWireBlock.class)
public abstract class TripWireBlockMixin {
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"), method = "playerWillDestroy")
	private boolean isShears(ItemStack stack, Item item) {
		return stack.is(item) || ShearsItemBuilder.isKJSSheers(item);
	}
}
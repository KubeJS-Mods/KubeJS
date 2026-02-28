package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.CommonProperties;
import net.neoforged.neoforge.transfer.item.ItemAccessItemHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ItemStacksResourceHandler.class, ItemAccessItemHandler.class})
public class ItemStackHandlerMixin {
	@Inject(method = "getCapacity(ILnet/neoforged/neoforge/transfer/item/ItemResource;)I", at = @At("RETURN"), cancellable = true)
	private void kjs$maxSlotSize(int index, ItemResource resource, CallbackInfoReturnable<Integer> cir) {
		if (!resource.isEmpty() && CommonProperties.get().removeSlotLimit) {
			cir.setReturnValue(1_000_000_000);
		}
	}
}
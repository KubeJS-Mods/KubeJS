package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.CommonProperties;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.transfer.item.ItemAccessItemHandler;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin({ItemStacksResourceHandler.class, ItemAccessItemHandler.class})
public class ItemStackHandlerMixin {
	@ModifyConstant(method = "getCapacity(ILnet/neoforged/neoforge/transfer/item/ItemResource;)I", constant = @Constant(intValue = Item.ABSOLUTE_MAX_STACK_SIZE))
	private int kjs$maxSlotSize(int original) {
		return CommonProperties.get().getMaxSlotSize(original);
	}
}
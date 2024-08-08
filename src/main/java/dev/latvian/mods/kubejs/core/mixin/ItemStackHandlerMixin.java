package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.CommonProperties;
import net.neoforged.neoforge.items.ComponentItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin({ItemStackHandler.class, ComponentItemHandler.class})
public class ItemStackHandlerMixin {
	@ModifyConstant(method = "getSlotLimit", constant = @Constant(intValue = 99))
	private int kjs$maxSlotSize(int original) {
		return CommonProperties.get().getMaxSlotSize(original);
	}
}

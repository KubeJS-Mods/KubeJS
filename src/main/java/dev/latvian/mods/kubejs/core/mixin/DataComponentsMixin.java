package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.CommonProperties;
import net.minecraft.core.component.DataComponents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(DataComponents.class)
public class DataComponentsMixin {
	@ModifyConstant(method = "lambda$static$1", constant = @Constant(intValue = 99))
	private static int kjs$maxSlotSize(int original) {
		return CommonProperties.get().getMaxSlotSize(original);
	}

	@ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 64))
	private static int kjs$maxStackSize(int original) {
		return CommonProperties.get().getMaxStackSize(original);
	}
}

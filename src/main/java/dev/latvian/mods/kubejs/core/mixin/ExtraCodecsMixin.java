package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.CommonProperties;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ExtraCodecs.class)
public class ExtraCodecsMixin {
	@ModifyArg(
		method = "intRange(II)Lcom/mojang/serialization/Codec;",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ExtraCodecs;intRangeWithMessage(IILjava/util/function/Function;)Lcom/mojang/serialization/Codec;"),
		index = 1
	)
	private static int kjs$maxSlotSize(int original) {
		return original == 99 ? CommonProperties.get().getMaxSlotSize(original) : original;
	}
}
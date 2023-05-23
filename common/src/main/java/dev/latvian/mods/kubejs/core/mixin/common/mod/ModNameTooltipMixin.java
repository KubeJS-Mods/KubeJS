package dev.latvian.mods.kubejs.core.mixin.common.mod;

import dev.latvian.mods.kubejs.util.CustomModNames;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "mezz/modnametooltip/TooltipEventHandler")
public abstract class ModNameTooltipMixin {
	@Inject(method = "getModName(Ljava/lang/String;)Ljava/lang/String;", at = @At("HEAD"), cancellable = true, remap = false)
	private static void kjs$modId(String modId, CallbackInfoReturnable<String> cir) {
		var r = CustomModNames.get(modId);

		if (!r.isEmpty()) {
			cir.setReturnValue(r);
		}
	}
}

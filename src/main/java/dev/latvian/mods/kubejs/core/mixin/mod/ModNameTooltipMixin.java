package dev.latvian.mods.kubejs.core.mixin.mod;

import dev.latvian.mods.kubejs.script.PlatformWrapper;
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
		var r = PlatformWrapper.getMods().get(modId);

		if (r != null && !r.getCustomName().isEmpty()) {
			cir.setReturnValue(r.getCustomName());
		}
	}
}

package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.util.KubeJSBackgroundThread;
import net.minecraft.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Util.class)
public abstract class UtilMixin {
	@Inject(method = "shutdownExecutors", at = @At("RETURN"))
	private static void shutdownExecutorsKJS(CallbackInfo ci) {
		KubeJSBackgroundThread.running = false;
	}
}

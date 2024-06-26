package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.script.KubeJSBackgroundThread;
import dev.latvian.mods.kubejs.script.ScriptType;
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

		for (var value : ScriptType.values()) {
			value.console.flush(true);
		}
	}
}

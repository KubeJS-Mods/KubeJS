package dev.latvian.kubejs.mixin.common;

import dev.latvian.kubejs.util.ConsoleJS;
import net.minecraft.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author LatvianModder
 */
@Mixin(Util.class)
public abstract class UtilMixin
{
	@Inject(method = "shutdownExecutors", at = @At("RETURN"))
	private static void shutdownExecutorsKJS(CallbackInfo ci)
	{
		ConsoleJS.shutdownLogWriter();
	}
}

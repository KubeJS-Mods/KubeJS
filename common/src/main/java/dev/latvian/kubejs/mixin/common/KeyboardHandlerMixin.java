package dev.latvian.kubejs.mixin.common;

import dev.latvian.kubejs.client.KubeJSClient;
import net.minecraft.client.KeyboardHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author LatvianModder
 */
@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin
{
	@Inject(method = "handleDebugKeys", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;reloadResourcePacks()Ljava/util/concurrent/CompletableFuture;", shift = At.Shift.BEFORE))
	private void reloadResources(int i, CallbackInfoReturnable<Boolean> ci)
	{
		KubeJSClient.reloadClientScripts();
	}
}

package dev.latvian.kubejs.mixin.common;

import dev.latvian.kubejs.server.KubeJSReloadListener;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ServerResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author LatvianModder
 */
@Mixin(ServerResources.class)
public abstract class ServerResourcesMixin {
	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(RegistryAccess registryAccess, Commands.CommandSelection commandSelection, int i, CallbackInfo ci) {
		KubeJSReloadListener.resources = (ServerResources) (Object) this;
	}
}

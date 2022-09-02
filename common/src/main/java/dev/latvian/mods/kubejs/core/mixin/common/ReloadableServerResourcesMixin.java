package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.item.ingredient.TagContext;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author LatvianModder
 */
@Mixin(ReloadableServerResources.class)
public abstract class ReloadableServerResourcesMixin {
	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(RegistryAccess.Frozen frozen, Commands.CommandSelection commandSelection, int i, CallbackInfo ci) {
		ServerScriptManager.instance.updateResources((ReloadableServerResources) (Object) this);
	}

	@Inject(method = "updateRegistryTags(Lnet/minecraft/core/RegistryAccess;)V", at = @At("RETURN"))
	public void updateRegistryTags(RegistryAccess registryAccess, CallbackInfo ci) {
		TagContext.INSTANCE.setValue(TagContext.REGISTRY);
	}
}

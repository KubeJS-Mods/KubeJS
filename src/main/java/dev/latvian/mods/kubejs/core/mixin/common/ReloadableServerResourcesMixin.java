package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.item.ingredient.TagContext;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.tags.TagManager;
import net.minecraft.world.flag.FeatureFlagSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ReloadableServerResources.class)
public abstract class ReloadableServerResourcesMixin {
	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(RegistryAccess.Frozen frozen, FeatureFlagSet featureFlagSet, Commands.CommandSelection commandSelection, int i, CallbackInfo ci) {
		ServerScriptManager.instance.updateResources((ReloadableServerResources) (Object) this, frozen);
	}

	@Inject(method = "updateRegistryTags(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/tags/TagManager$LoadResult;)V", at = @At("RETURN"))
	private static void updateRegistryTags(RegistryAccess registryAccess, TagManager.LoadResult<?> result, CallbackInfo ci) {
		TagContext.INSTANCE.setValue(TagContext.usingRegistry(registryAccess));
	}
}

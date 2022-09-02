package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.client.ClientProperties;
import dev.latvian.mods.kubejs.client.KubeJSClientResourcePack;
import dev.latvian.mods.kubejs.core.MinecraftClientKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.PackRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * @author LatvianModder
 */
@Mixin(Minecraft.class)
@RemapPrefixForJS("kjs$")
public abstract class MinecraftMixin implements MinecraftClientKJS {
	@Inject(method = "createTitle", at = @At("HEAD"), cancellable = true)
	private void kjs$createTitle(CallbackInfoReturnable<String> ci) {
		var s = ClientProperties.get().title;

		if (!s.isEmpty()) {
			ci.setReturnValue(s);
		}
	}

	@Redirect(method = {"reloadResourcePacks(Z)Ljava/util/concurrent/CompletableFuture;", "<init>"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackRepository;openAllSelected()Ljava/util/List;"))
	private List<PackResources> kjs$loadPacks(PackRepository repository) {
		return KubeJSClientResourcePack.inject(repository.openAllSelected());
	}
}
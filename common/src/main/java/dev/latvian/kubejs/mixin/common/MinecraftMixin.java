package dev.latvian.kubejs.mixin.common;

import dev.latvian.kubejs.client.ClientProperties;
import dev.latvian.kubejs.client.KubeJSClientResourcePack;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.PackRepository;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * @author LatvianModder
 */
@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
	@Shadow
	@Final
	private PackRepository resourcePackRepository;

	@Inject(method = "createTitle", at = @At("HEAD"), cancellable = true)
	private void getWindowTitleKJS(CallbackInfoReturnable<String> ci) {
		String s = ClientProperties.get().title;

		if (!s.isEmpty()) {
			ci.setReturnValue(s);
		}
	}

	@Redirect(method = {"reloadResourcePacks(Z)Ljava/util/concurrent/CompletableFuture;", "<init>"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackRepository;openAllSelected()Ljava/util/List;"))
	private List<PackResources> loadPacksKJS(PackRepository repository) {
		return KubeJSClientResourcePack.inject(repository.openAllSelected());
	}
}
package dev.latvian.mods.kubejs.mixin.common;

import dev.latvian.mods.kubejs.client.ClientProperties;
import dev.latvian.mods.kubejs.client.KubeJSClientResourcePack;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.PackRepository;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
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
		var s = ClientProperties.get().title;

		if (!s.isEmpty()) {
			ci.setReturnValue(s);
		}
	}

	@ModifyVariable(method = {"<init>", "reloadResourcePacks(Z)Ljava/util/concurrent/CompletableFuture;"}, at = @At("STORE"))
	private List<PackResources> injectKubeJSResources(List<PackResources> original) {
		return KubeJSClientResourcePack.inject(original);
	}
}
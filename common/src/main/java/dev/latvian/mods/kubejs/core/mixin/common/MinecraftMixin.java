package dev.latvian.mods.kubejs.core.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.latvian.mods.kubejs.client.ClientProperties;
import dev.latvian.mods.kubejs.client.GeneratedClientResourcePack;
import dev.latvian.mods.kubejs.core.MinecraftClientKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.PackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

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

	@ModifyExpressionValue(
		method = {"reloadResourcePacks(Z)Ljava/util/concurrent/CompletableFuture;", "<init>"},
		at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackRepository;openAllSelected()Ljava/util/List;")
	)
	private List<PackResources> kjs$loadPacks(List<PackResources> resources) {
		return GeneratedClientResourcePack.inject(kjs$self(), resources);
	}

	@Inject(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;swing(Lnet/minecraft/world/InteractionHand;)V", shift = At.Shift.AFTER))
	private void kjs$startAttack(CallbackInfoReturnable<Boolean> cir) {
		kjs$startAttack0();
	}

	@Inject(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;startUseItem()V", ordinal = 0, shift = At.Shift.AFTER))
	private void kjs$startUseItem(CallbackInfo ci) {
		kjs$startUseItem0();
	}
}
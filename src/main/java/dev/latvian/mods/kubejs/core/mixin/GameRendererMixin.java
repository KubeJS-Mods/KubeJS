package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.client.KubeSessionData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@Shadow
	@Final
	Minecraft minecraft;

	@Shadow
	@Nullable
	PostChain postEffect;

	@Shadow
	public abstract void loadEffect(ResourceLocation resourceLocation);

	@Inject(method = "checkEntityPostEffect", at = @At("HEAD"), cancellable = true)
	private void kjs$checkEntityPostEffect(CallbackInfo ci) {
		var data = KubeSessionData.of(minecraft);

		if (data != null && data.activePostShader != null) {
			if (postEffect != null) {
				postEffect.close();
			}

			loadEffect(data.activePostShader);
			ci.cancel();
		}
	}
}

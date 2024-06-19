package dev.latvian.mods.kubejs.core.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.latvian.mods.kubejs.client.KubeJSClient;
import dev.latvian.mods.kubejs.client.KubedexHighlight;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
	@Shadow
	@Final
	private Minecraft minecraft;

	@Inject(method = "initOutline", at = @At("RETURN"))
	private void kjs$loadPostChains(CallbackInfo ci) {
		KubeJSClient.loadPostChains(minecraft);
	}

	@Inject(method = "resize", at = @At("RETURN"))
	private void kjs$resizePostChains(int width, int height, CallbackInfo ci) {
		KubeJSClient.resizePostChains(width, height);
	}

	@Inject(method = "renderHitOutline", at = @At("HEAD"), cancellable = true)
	private void kjs$highlightBlock(PoseStack poseStack, VertexConsumer consumer, Entity entity, double camX, double camY, double camZ, BlockPos pos, BlockState state, CallbackInfo ci) {
		if (KubedexHighlight.INSTANCE.mode.cancelHighlight) {
			ci.cancel();
		}
	}
}

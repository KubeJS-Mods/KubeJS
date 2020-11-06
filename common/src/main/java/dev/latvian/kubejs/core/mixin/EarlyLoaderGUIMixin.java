package dev.latvian.kubejs.core.mixin;

import com.mojang.blaze3d.platform.Window;
import dev.latvian.kubejs.client.ClientProperties;
import net.minecraftforge.fml.client.EarlyLoaderGUI;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author LatvianModder
 */
@Mixin(EarlyLoaderGUI.class)
public abstract class EarlyLoaderGUIMixin
{
	@Shadow(remap = false)
	@Final
	private Window window;

	@ModifyArg(method = "renderMemoryInfo", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/client/EarlyLoaderGUI;renderMessage(Ljava/lang/String;[FIF)V"), index = 1)
	private float[] memoryColorKJS(float[] color)
	{
		return ClientProperties.get().getMemoryColor(color);
	}

	@ModifyArg(method = "renderMessages", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/client/EarlyLoaderGUI;renderMessage(Ljava/lang/String;[FIF)V"), index = 1)
	private float[] logColorKJS(float[] color)
	{
		return ClientProperties.get().getLogColor(color);
	}

	@ModifyArg(method = "renderTick", remap = false, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;clearColor(FFFF)V"), index = 0)
	private float backgroundRKJS(float c)
	{
		return ClientProperties.get().getBackgroundColor(c, 0);
	}

	@ModifyArg(method = "renderTick", remap = false, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;clearColor(FFFF)V"), index = 1)
	private float backgroundGKJS(float c)
	{
		return ClientProperties.get().getBackgroundColor(c, 1);
	}

	@ModifyArg(method = "renderTick", remap = false, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;clearColor(FFFF)V"), index = 2)
	private float backgroundBKJS(float c)
	{
		return ClientProperties.get().getBackgroundColor(c, 2);
	}

	@Inject(method = "renderBackground", remap = false, at = @At("HEAD"), cancellable = true)
	private void renderBackgroundKJS(CallbackInfo ci)
	{
		ci.cancel();
	}
}
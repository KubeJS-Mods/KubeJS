package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.core.EarlyLoaderGUIKJS;
import net.minecraft.client.MainWindow;
import net.minecraftforge.fml.client.EarlyLoaderGUI;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * @author LatvianModder
 */
@Mixin(EarlyLoaderGUI.class)
public abstract class EarlyLoaderGUIMixin implements EarlyLoaderGUIKJS
{
	@Shadow(remap = false)
	@Final
	private MainWindow window;

	@ModifyArg(method = "renderMemoryInfo", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/client/EarlyLoaderGUI;renderMessage(Ljava/lang/String;[FIF)V"), index = 1)
	private float[] memoryColorKJS(float[] color)
	{
		return getMemoryColorKJS(color);
	}

	@ModifyArg(method = "renderMessages", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/client/EarlyLoaderGUI;renderMessage(Ljava/lang/String;[FIF)V"), index = 1)
	private float[] logColorKJS(float[] color)
	{
		return getLogColorKJS(color);
	}

	@ModifyArg(method = "renderTick", remap = false, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;clearColor(FFFF)V"), index = 0)
	private float backgroundRKJS(float c)
	{
		return getBackgroundColorKJS(c, 0);
	}

	@ModifyArg(method = "renderTick", remap = false, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;clearColor(FFFF)V"), index = 1)
	private float backgroundGKJS(float c)
	{
		return getBackgroundColorKJS(c, 1);
	}

	@ModifyArg(method = "renderTick", remap = false, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;clearColor(FFFF)V"), index = 2)
	private float backgroundBKJS(float c)
	{
		return getBackgroundColorKJS(c, 2);
	}
}
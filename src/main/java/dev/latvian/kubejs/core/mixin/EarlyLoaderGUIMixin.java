package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.core.EarlyLoaderGUIKJS;
import net.minecraftforge.fml.client.EarlyLoaderGUI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * @author LatvianModder
 */
@Mixin(EarlyLoaderGUI.class)
public abstract class EarlyLoaderGUIMixin implements EarlyLoaderGUIKJS
{
	@ModifyArg(method = "renderMemoryInfo", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/client/EarlyLoaderGUI;renderMessage(Ljava/lang/String;[FIF)V"), index = 1)
	private float[] memoryColorKJS(float[] color)
	{
		return getNewMemoryColorKJS(color);
	}

	@ModifyArg(method = "renderMessages", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/client/EarlyLoaderGUI;renderMessage(Ljava/lang/String;[FIF)V"), index = 1)
	private float[] logColorKJS(float[] color)
	{
		return getNewLogColorKJS(color);
	}
}
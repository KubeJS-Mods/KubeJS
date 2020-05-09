package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.client.ClientProperties;
import dev.latvian.kubejs.core.EarlyLoaderGUIKJS;
import net.minecraft.client.MainWindow;
import net.minecraftforge.fml.client.EarlyLoaderGUI;
import org.lwjgl.opengl.GL11;
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
public abstract class EarlyLoaderGUIMixin implements EarlyLoaderGUIKJS
{
	@Shadow(remap = false)
	@Final
	private MainWindow window;

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

	@Inject(method = "setupMatrix", remap = false, at = @At("RETURN"))
	private void setupMatrix(CallbackInfo ci)
	{
		int w = window.getScaledWidth();
		int h = window.getScaledHeight();

		int color = ClientProperties.get().backgroundColor;

		GL11.glBegin(GL11.GL_QUADS);
		GL11.glColor4f((color >> 16 & 255) / 255F, (color >> 8 & 255) / 255F, (color & 255) / 255F, 1F);
		GL11.glVertex2f(0F, 0F);
		GL11.glVertex2f(w, 0F);
		GL11.glVertex2f(w, h);
		GL11.glVertex2f(0F, h);
		GL11.glEnd();
	}
}
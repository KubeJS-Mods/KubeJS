package dev.latvian.kubejs.mixin.forge;

import dev.latvian.kubejs.client.ClientProperties;
import net.minecraft.client.gui.screens.LoadingOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * @author LatvianModder
 */
@Mixin(LoadingOverlay.class)
public abstract class ResourceLoadProgressGuiMixin
{
	@ModifyArg(method = "render",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/LoadingOverlay;fill(Lcom/mojang/blaze3d/vertex/PoseStack;IIIII)V"),
			index = 5)
	private int backgroundColorKJS(int color)
	{
		return ClientProperties.get().getBackgroundColor(color);
	}

	@ModifyArg(method = "drawProgressBar", at = @At(value = "INVOKE", ordinal = 0,
			target = "Lnet/minecraft/client/gui/screens/LoadingOverlay;fill(Lcom/mojang/blaze3d/vertex/PoseStack;IIIII)V"),
			index = 5)
	private int barColorKJS1(int color)
	{
		return ClientProperties.get().getBarBorderColor(color);
	}

	@ModifyArg(method = "drawProgressBar", at = @At(value = "INVOKE", ordinal = 1,
			target = "Lnet/minecraft/client/gui/screens/LoadingOverlay;fill(Lcom/mojang/blaze3d/vertex/PoseStack;IIIII)V"),
			index = 5)
	private int barColorKJS2(int color)
	{
		return ClientProperties.get().getBarBorderColor(color);
	}

	@ModifyArg(method = "drawProgressBar", at = @At(value = "INVOKE", ordinal = 2,
			target = "Lnet/minecraft/client/gui/screens/LoadingOverlay;fill(Lcom/mojang/blaze3d/vertex/PoseStack;IIIII)V"),
			index = 5)
	private int barColorKJS3(int color)
	{
		return ClientProperties.get().getBarBorderColor(color);
	}

	@ModifyArg(method = "drawProgressBar", at = @At(value = "INVOKE", ordinal = 3,
			target = "Lnet/minecraft/client/gui/screens/LoadingOverlay;fill(Lcom/mojang/blaze3d/vertex/PoseStack;IIIII)V"),
			index = 5)
	private int barColorKJS4(int color)
	{
		return ClientProperties.get().getBarBorderColor(color);
	}

	@ModifyArg(method = "drawProgressBar", at = @At(value = "INVOKE", ordinal = 4,
			target = "Lnet/minecraft/client/gui/screens/LoadingOverlay;fill(Lcom/mojang/blaze3d/vertex/PoseStack;IIIII)V"),
			index = 5)
	private int barBorderColorKJS(int color)
	{
		return ClientProperties.get().getBarColor(color);
	}
}
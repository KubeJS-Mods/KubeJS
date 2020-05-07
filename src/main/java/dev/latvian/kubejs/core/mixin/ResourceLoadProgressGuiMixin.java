package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.core.ResourceLoadProgressGuiKJS;
import net.minecraft.client.gui.ResourceLoadProgressGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * @author LatvianModder
 */
@Mixin(ResourceLoadProgressGui.class)
public abstract class ResourceLoadProgressGuiMixin implements ResourceLoadProgressGuiKJS
{
	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ResourceLoadProgressGui;fill(IIIII)V"), index = 4)
	private int backgroundColorKJS(int color)
	{
		return getNewBackgroundColorKJS(color);
	}

	@ModifyArg(method = "renderProgressBar", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/gui/ResourceLoadProgressGui;fill(IIIII)V"), index = 4)
	private int barBorderColorKJS(int color)
	{
		return getNewBarBorderColorKJS(color);
	}

	@ModifyArg(method = "renderProgressBar", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/client/gui/ResourceLoadProgressGui;fill(IIIII)V"), index = 4)
	private int barBackgroundColorKJS(int color)
	{
		return getNewBarBackgroundColorKJS(color);
	}

	@ModifyArg(method = "renderProgressBar", at = @At(value = "INVOKE", ordinal = 2, target = "Lnet/minecraft/client/gui/ResourceLoadProgressGui;fill(IIIII)V"), index = 4)
	private int barColorKJS(int color)
	{
		return getNewBarColorKJS(color);
	}
}
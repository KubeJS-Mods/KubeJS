package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.client.ClientProperties;
import net.minecraft.client.gui.ResourceLoadProgressGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * @author LatvianModder
 */
@Mixin(ResourceLoadProgressGui.class)
public abstract class ResourceLoadProgressGuiMixin
{
	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ResourceLoadProgressGui;fill(Lcom/mojang/blaze3d/matrix/MatrixStack;IIIII)V"), index = 5)
	private int backgroundColorKJS(int color)
	{
		return ClientProperties.get().getBackgroundColor(color);
	}

	@ModifyArg(method = "func_238629_a_", remap = false, at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/gui/ResourceLoadProgressGui;fill(Lcom/mojang/blaze3d/matrix/MatrixStack;IIIII)V"), index = 5)
	private int barColorKJS1(int color)
	{
		return ClientProperties.get().getBarBorderColor(color);
	}

	@ModifyArg(method = "func_238629_a_", remap = false, at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/client/gui/ResourceLoadProgressGui;fill(Lcom/mojang/blaze3d/matrix/MatrixStack;IIIII)V"), index = 5)
	private int barColorKJS2(int color)
	{
		return ClientProperties.get().getBarBorderColor(color);
	}

	@ModifyArg(method = "func_238629_a_", remap = false, at = @At(value = "INVOKE", ordinal = 2, target = "Lnet/minecraft/client/gui/ResourceLoadProgressGui;fill(Lcom/mojang/blaze3d/matrix/MatrixStack;IIIII)V"), index = 5)
	private int barColorKJS3(int color)
	{
		return ClientProperties.get().getBarBorderColor(color);
	}

	@ModifyArg(method = "func_238629_a_", remap = false, at = @At(value = "INVOKE", ordinal = 3, target = "Lnet/minecraft/client/gui/ResourceLoadProgressGui;fill(Lcom/mojang/blaze3d/matrix/MatrixStack;IIIII)V"), index = 5)
	private int barColorKJS4(int color)
	{
		return ClientProperties.get().getBarBorderColor(color);
	}

	@ModifyArg(method = "func_238629_a_", remap = false, at = @At(value = "INVOKE", ordinal = 4, target = "Lnet/minecraft/client/gui/ResourceLoadProgressGui;fill(Lcom/mojang/blaze3d/matrix/MatrixStack;IIIII)V"), index = 5)
	private int barBorderColorKJS(int color)
	{
		return ClientProperties.get().getBarColor(color);
	}
}
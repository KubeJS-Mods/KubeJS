package dev.latvian.mods.kubejs.core.mixin.common;

import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Screen.class)
public abstract class ScreenMixin {
	/*
	@ModifyConstant(method = "renderDirtBackground", constant = @Constant(intValue = 64), slice = @Slice(
		from = @At(value = "INVOKE", ordinal = 0, target = "Lcom/mojang/blaze3d/vertex/BufferBuilder;begin(Lcom/mojang/blaze3d/vertex/VertexFormat$Mode;Lcom/mojang/blaze3d/vertex/VertexFormat;)V"),
		to = @At(value = "INVOKE", ordinal = 0, target = "Lcom/mojang/blaze3d/vertex/Tesselator;end()V")
	))
	private int backgroundBrightnessKJS(int old) {
		return ClientProperties.get().getMenuBackgroundBrightness();
	}

	@ModifyConstant(method = "renderDirtBackground", constant = @Constant(floatValue = 32F))
	private float backgroundScaleKJS(float old) {
		return ClientProperties.get().getMenuBackgroundScale();
	}
*/
}

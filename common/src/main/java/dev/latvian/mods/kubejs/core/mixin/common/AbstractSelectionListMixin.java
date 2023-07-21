package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.client.ClientProperties;
import net.minecraft.client.gui.components.AbstractSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(AbstractSelectionList.class)
public abstract class AbstractSelectionListMixin<E extends AbstractSelectionList.Entry<E>> {
	@ModifyConstant(method = "render", constant = @Constant(intValue = 32), slice = @Slice(
		from = @At(value = "INVOKE", ordinal = 0, target = "Lcom/mojang/blaze3d/vertex/BufferBuilder;begin(Lcom/mojang/blaze3d/vertex/VertexFormat$Mode;Lcom/mojang/blaze3d/vertex/VertexFormat;)V"),
		to = @At(value = "INVOKE", ordinal = 0, target = "Lcom/mojang/blaze3d/vertex/Tesselator;end()V")
	))
	private int innerBackgroundBrightnessKJS(int old) {
		return ClientProperties.get().getMenuInnerBackgroundBrightness();
	}

	@ModifyConstant(method = "render", constant = @Constant(intValue = 64), slice = @Slice(
		from = @At(value = "INVOKE", ordinal = 1, target = "Lcom/mojang/blaze3d/vertex/BufferBuilder;begin(Lcom/mojang/blaze3d/vertex/VertexFormat$Mode;Lcom/mojang/blaze3d/vertex/VertexFormat;)V"),
		to = @At(value = "INVOKE", ordinal = 1, target = "Lcom/mojang/blaze3d/vertex/Tesselator;end()V")
	))
	private int backgroundBrightnessKJS(int old) {
		return ClientProperties.get().getMenuBackgroundBrightness();
	}

	@ModifyConstant(method = "render", constant = @Constant(floatValue = 32F))
	private float backgroundScaleKJS(float old) {
		return ClientProperties.get().getMenuBackgroundScale();
	}
}

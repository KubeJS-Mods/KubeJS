package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.client.ClientProperties;
import dev.latvian.mods.kubejs.client.KubeJSClient;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin {
	@Unique
	private final int[] kjs$itemSize = new int[3];

	@Inject(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I", shift = At.Shift.BEFORE))
	private void kjs$beforeDrawSize(Font font, ItemStack stack, int x, int y, String text, CallbackInfo ci) {
		if (text == null && CommonProperties.get().removeSlotLimit && ClientProperties.get().customStackSizeText && stack.getCount() > 1) {
			kjs$itemSize[0] = stack.getCount();
			kjs$itemSize[1] = x;
			kjs$itemSize[2] = y;
		} else {
			kjs$itemSize[0] = 0;
		}
	}

	@Inject(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I", shift = At.Shift.AFTER))
	private void kjs$afterDrawSize(Font font, ItemStack stack, int x, int y, String text, CallbackInfo ci) {
		kjs$itemSize[0] = 0;
	}

	@Inject(method = "drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I", at = @At("HEAD"), cancellable = true)
	private void kjs$drawString(Font font, String text, int x, int y, int color, boolean dropShadow, CallbackInfoReturnable<Integer> cir) {
		if (kjs$itemSize[0] != 0) {
			cir.setReturnValue(KubeJSClient.drawStackSize((GuiGraphics) (Object) this, font, kjs$itemSize[0], kjs$itemSize[1], kjs$itemSize[2], color, dropShadow));
		}
	}
}

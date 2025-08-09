package dev.latvian.mods.kubejs.core.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.client.ClientProperties;
import dev.latvian.mods.kubejs.client.KubeJSClient;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin {
	@WrapWithCondition(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I"))
	private boolean kjs$drawSize(GuiGraphics instance, Font font, String text, int x, int y, int color, boolean dropShadow, Font pFont, ItemStack stack, int pX, int pY, String pText) {
		if (pText == null && CommonProperties.get().removeSlotLimit && ClientProperties.get().customStackSizeText && stack.getCount() > 1) {
			KubeJSClient.drawStackSize(instance, font, stack.getCount(), pX, pY, color, dropShadow);
			return false;
		}
		return true;
	}
}

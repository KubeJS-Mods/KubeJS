package dev.latvian.mods.kubejs.core.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.platform.IconSet;
import com.mojang.blaze3d.platform.Window;
import dev.latvian.mods.kubejs.core.WindowKJS;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.IoSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Mixin(Window.class)
public class WindowMixin implements WindowKJS {
	@ModifyExpressionValue(method = "setIcon", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/IconSet;getStandardIcons(Lnet/minecraft/server/packs/PackResources;)Ljava/util/List;"))
	private List<IoSupplier<InputStream>> kjs$icons(List<IoSupplier<InputStream>> original, PackResources packResources, IconSet set) throws IOException {
		return kjs$loadIcons(original);
	}
}
package dev.latvian.mods.kubejs.mixin.common;

import com.mojang.blaze3d.platform.Window;
import dev.latvian.mods.kubejs.client.ClientProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.InputStream;

/**
 * @author LatvianModder
 */
@Mixin(Window.class)
public class MainWindowMixin {
	@Inject(method = "setIcon", at = @At("HEAD"), cancellable = true)
	private void setWindowIcon(InputStream icon16, InputStream icon32, CallbackInfo ci) {
		if (ClientProperties.get().cancelIconUpdate()) {
			ci.cancel();
		}
	}
}
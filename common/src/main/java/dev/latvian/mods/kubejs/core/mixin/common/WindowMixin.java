package dev.latvian.mods.kubejs.core.mixin.common;

import com.mojang.blaze3d.platform.Window;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Window.class)
public class WindowMixin {
	/*
	@Inject(method = "setIcon", at = @At("HEAD"), cancellable = true)
	private void setWindowIcon(InputStream icon16, InputStream icon32, CallbackInfo ci) {
		if (ClientProperties.get().cancelIconUpdate()) {
			ci.cancel();
		}
	}
	 */
}
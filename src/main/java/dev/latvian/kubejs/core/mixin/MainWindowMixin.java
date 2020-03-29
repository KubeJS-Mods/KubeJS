package dev.latvian.kubejs.core.mixin;

import dev.latvian.kubejs.client.PackOverrides;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.InputStream;

/**
 * @author LatvianModder
 */
@Mixin(MainWindow.class)
public class MainWindowMixin
{
	@Inject(method = "setWindowIcon", at = @At("HEAD"), cancellable = true)
	private void setWindowIcon(InputStream icon16, InputStream icon32, CallbackInfo ci)
	{
		if (PackOverrides.get(Minecraft.getInstance()).cancelIconUpdate())
		{
			ci.cancel();
		}
	}
}
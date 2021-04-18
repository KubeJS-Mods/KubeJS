package dev.latvian.kubejs.mixin.common;

import dev.latvian.kubejs.client.KubeJSClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(Options.class)
public class OptionsMixin {
	@Shadow
	@Final
	private File optionsFile;

	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;load()V", shift = At.Shift.BEFORE))
	private void test(Minecraft minecraft, File file, CallbackInfo ci) {
		KubeJSClient.copyDefaultOptionsFile(optionsFile);
	}
}

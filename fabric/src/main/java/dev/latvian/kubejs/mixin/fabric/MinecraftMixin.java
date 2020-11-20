package dev.latvian.kubejs.mixin.fabric;

import dev.latvian.kubejs.client.ClientProperties;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author LatvianModder
 */
@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
	@Inject(method = "createTitle", at = @At("HEAD"), cancellable = true)
	private void getWindowTitle(CallbackInfoReturnable<String> ci)
	{
		String s = ClientProperties.get().title;

		if (!s.isEmpty())
		{
			ci.setReturnValue(s);
		}
	}
}
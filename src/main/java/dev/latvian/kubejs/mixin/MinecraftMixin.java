package dev.latvian.kubejs.mixin;

import dev.latvian.kubejs.client.ClientWrapper;
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
	@Inject(method = "func_230149_ax_", at = @At("RETURN"), remap = false, cancellable = true)
	public void getWindowTitle(CallbackInfoReturnable<String> ci)
	{
		if (!ClientWrapper.title.isEmpty())
		{
			ci.setReturnValue(ClientWrapper.title);
		}
	}
}
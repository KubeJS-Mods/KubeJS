package dev.latvian.mods.kubejs.core.mixin;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.util.WithCodec;
import dev.latvian.mods.rhino.Context;
import net.minecraft.network.chat.ClickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClickEvent.class)
public abstract class ClickEventMixin implements WithCodec {
	@Shadow
	public abstract String getValue();
	
	@Inject(method = "getValue", at = @At("RETURN"), cancellable = true)
	private void kubejs$getValueWithQuoteStyle(CallbackInfoReturnable<String> cir) {
		String value = cir.getReturnValue();
		
		if (CommonProperties.get().useDoubleQuotes && value != null && value.startsWith("'") && value.endsWith("'")) {
			cir.setReturnValue("\"" + value.substring(1, value.length() - 1) + "\"");
		}
	}

	@Override
	public Codec<?> getCodec(Context cx) {
		return ClickEvent.CODEC;
	}
}

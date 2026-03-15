package dev.latvian.mods.kubejs.core.mixin;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.util.WithCodec;
import dev.latvian.mods.rhino.Context;
import net.minecraft.network.chat.ClickEvent;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClickEvent.class)
public interface ClickEventMixin extends WithCodec {
	@Override
	default Codec<?> getCodec(Context cx) {
		return ClickEvent.CODEC;
	}
}

package dev.latvian.mods.kubejs.core.mixin;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.util.WithCodec;
import dev.latvian.mods.rhino.Context;
import net.minecraft.network.chat.ClickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClickEvent.class)
public abstract class ClickEventMixin implements WithCodec {
	@Shadow
	public abstract ClickEvent.Action getAction();

	@Shadow
	public abstract String getValue();

	@Override
	public Codec<?> getCodec(Context cx) {
		return ClickEvent.CODEC;
	}
}

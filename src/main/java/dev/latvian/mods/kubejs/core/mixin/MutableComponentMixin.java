package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.ComponentKJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MutableComponent.class)
@RemapPrefixForJS("kjs$")
public abstract class MutableComponentMixin implements ComponentKJS {

	// hidden to avoid ambiguity, the type wrapper should wrap strings to TextComponent anyways
	@HideFromJS
	@Shadow
	public abstract MutableComponent append(String string);
}

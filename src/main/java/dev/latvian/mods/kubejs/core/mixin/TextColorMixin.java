package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.color.KubeColor;
import net.minecraft.network.chat.TextColor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TextColor.class)
public abstract class TextColorMixin implements KubeColor {
	@Shadow
	@Final
	private int value;

	@Override
	public int kjs$getARGB() {
		return 0xFF000000 | value;
	}

	@Override
	public int kjs$getRGB() {
		return value;
	}

	@Override
	@Invoker("serialize")
	public abstract String kjs$serialize();
}

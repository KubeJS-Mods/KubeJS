package dev.latvian.kubejs.mixin.common;

import dev.latvian.kubejs.util.ColorKJS;
import net.minecraft.network.chat.TextColor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TextColor.class)
public abstract class TextColorMixin implements ColorKJS {
	@Shadow
	@Final
	private int value;

	@Override
	public int getArgbKJS() {
		return 0xFF000000 | value;
	}

	@Override
	public int getRgbKJS() {
		return value;
	}

	@Override
	public String getSerializeKJS() {
		return serialize();
	}

	@Shadow
	public abstract String serialize();
}

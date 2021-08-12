package dev.latvian.kubejs.mixin.common;

import dev.latvian.kubejs.util.ColorKJS;
import net.minecraft.ChatFormatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChatFormatting.class)
public abstract class ChatFormattingMixin implements ColorKJS {
	@Shadow
	@Final
	private Integer color;

	@Override
	public int getArgbKJS() {
		return color == null ? 0xFF000000 : (0xFF000000 | color);
	}

	@Override
	public int getRgbKJS() {
		return color == null ? 0 : color;
	}
}

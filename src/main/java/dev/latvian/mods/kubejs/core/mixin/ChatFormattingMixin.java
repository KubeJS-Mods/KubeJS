package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.color.Color;
import net.minecraft.ChatFormatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChatFormatting.class)
public abstract class ChatFormattingMixin implements Color {
	@Shadow
	@Final
	private Integer color;

	@Override
	public int getArgbJS() {
		return color == null ? 0xFF000000 : (0xFF000000 | color);
	}

	@Override
	public int getRgbJS() {
		return color == null ? 0 : color;
	}
}

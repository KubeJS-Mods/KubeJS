package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.color.KubeColor;
import net.minecraft.ChatFormatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChatFormatting.class)
public abstract class ChatFormattingMixin implements KubeColor {
	@Shadow
	@Final
	private Integer color;

	@Override
	public int kjs$getARGB() {
		return color == null ? 0xFF000000 : (0xFF000000 | color);
	}

	@Override
	public int kjs$getRGB() {
		return color == null ? 0 : color;
	}
}

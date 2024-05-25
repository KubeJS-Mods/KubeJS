package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.color.Color;
import net.minecraft.network.chat.TextColor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TextColor.class)
public abstract class TextColorMixin implements Color {
	@Shadow
	@Final
	private int value;

	@Override
	public int getArgbJS() {
		return 0xFF000000 | value;
	}

	@Override
	public int getRgbJS() {
		return value;
	}

	@Override
	public String getSerializeJS() {
		return serialize();
	}

	@Shadow
	public abstract String serialize();
}

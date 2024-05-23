package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.color.Color;
import net.minecraft.world.item.DyeColor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DyeColor.class)
public abstract class DyeColorMixin implements Color {
	@Shadow
	@Final
	private int textColor;

	@Shadow
	@Final
	private int fireworkColor;

	@Override
	public int getArgbJS() {
		return 0xFF000000 | textColor;
	}

	@Override
	public int getRgbJS() {
		return textColor;
	}

	@Override
	public int getFireworkColorJS() {
		return fireworkColor;
	}
}

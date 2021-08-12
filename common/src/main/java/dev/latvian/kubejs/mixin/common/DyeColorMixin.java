package dev.latvian.kubejs.mixin.common;

import dev.latvian.kubejs.util.ColorKJS;
import net.minecraft.world.item.DyeColor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DyeColor.class)
public abstract class DyeColorMixin implements ColorKJS {
	@Shadow
	@Final
	private int textColor;

	@Shadow
	@Final
	private int fireworkColor;

	@Override
	public int getArgbKJS() {
		return 0xFF000000 | textColor;
	}

	@Override
	public int getRgbKJS() {
		return textColor;
	}

	@Override
	public int getFireworkColorKJS() {
		return fireworkColor;
	}
}

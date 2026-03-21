package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.SpecialEquality;
import net.minecraft.nbt.StringTag;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Objects;

@Mixin(StringTag.class)
public abstract class StringTagMixin implements SpecialEquality {
	@Shadow
	@Final
	private String value;

	@Override
	public boolean specialEquals(Context cx, @Nullable Object o, boolean shallow) {
		return switch (o) {
			case CharSequence s -> Objects.equals(s, value);
			case null, default -> equals(o);
		};
	}
}

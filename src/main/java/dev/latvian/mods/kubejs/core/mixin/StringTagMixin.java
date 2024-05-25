package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.SpecialEquality;
import net.minecraft.nbt.StringTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StringTag.class)
public abstract class StringTagMixin implements SpecialEquality {
	@Shadow
	public abstract String getAsString();

	@Override
	public boolean specialEquals(Context cx, Object o, boolean shallow) {
		if (o instanceof CharSequence s) {
			return s.equals(getAsString());
		}

		return equals(o);
	}
}

package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.rhino.util.SpecialEquality;
import net.minecraft.nbt.NumericTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NumericTag.class)
public abstract class NumericTagMixin implements SpecialEquality {
	@Shadow
	public abstract byte getAsByte();

	@Shadow
	public abstract double getAsDouble();

	@Override
	public boolean specialEquals(Object o, boolean shallow) {
		if (o instanceof Boolean b) {
			return b == (getAsByte() != 0);
		} else if (o instanceof Number n1) {
			return getAsDouble() == n1.doubleValue();
		} else if (!shallow && o instanceof NumericTag n1) {
			return getAsDouble() == n1.getAsDouble();
		}

		return equals(o);
	}
}

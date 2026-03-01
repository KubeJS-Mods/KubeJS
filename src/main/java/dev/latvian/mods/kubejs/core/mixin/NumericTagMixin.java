package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.SpecialEquality;
import net.minecraft.nbt.NumericTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(NumericTag.class)
public interface NumericTagMixin extends SpecialEquality {
	@Shadow
	Optional<Byte> asByte();

	@Shadow
	Optional<Double> asDouble();

	@Override
	default boolean specialEquals(Context cx, Object o, boolean shallow) {
		return switch (o) {
			case Boolean b -> b == (asByte().get() != 0);
			case Number n1 -> asDouble().get() == n1.doubleValue();
			case NumericTag n1 when !shallow -> asDouble().get() == n1.asDouble().get();
			case null, default -> equals(o);
		};
	}
}

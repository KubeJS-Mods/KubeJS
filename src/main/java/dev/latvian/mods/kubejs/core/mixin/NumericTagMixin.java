package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.SpecialEquality;
import net.minecraft.nbt.NumericTag;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(NumericTag.class)
public interface NumericTagMixin extends SpecialEquality {
	@Shadow
	Optional<Byte> asByte();

	@Shadow
	Optional<Double> asDouble();

	@Shadow
	Number box();

	@Override
	default boolean specialEquals(Context cx, @Nullable Object o, boolean shallow) {
		return switch (o) {
			case Boolean b -> b == (asByte().orElseThrow() != 0);
			case Number n1 -> box().equals(n1);
			case NumericTag n1 when !shallow -> box().equals(n1.box());
			case null, default -> equals(o);
		};
	}
}

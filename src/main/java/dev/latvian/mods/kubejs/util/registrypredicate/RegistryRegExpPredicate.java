package dev.latvian.mods.kubejs.util.registrypredicate;

import dev.latvian.mods.kubejs.util.RegExpJS;
import net.minecraft.core.Holder;

import java.util.regex.Pattern;

public record RegistryRegExpPredicate<T>(Pattern pattern) implements RegistryPredicate<T> {
	@Override
	public boolean test(Holder<T> holder) {
		try {
			if (holder instanceof Holder.Reference<T> ref) {
				return pattern.matcher(ref.key().location().toString()).find();
			} else {
				return pattern.matcher(holder.unwrapKey().get().location().toString()).find();
			}
		} catch (Exception ex) {
			return false;
		}
	}

	@Override
	public String toString() {
		return RegExpJS.toRegExpString(pattern);
	}
}

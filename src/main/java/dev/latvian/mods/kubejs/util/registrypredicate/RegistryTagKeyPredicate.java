package dev.latvian.mods.kubejs.util.registrypredicate;

import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;

public record RegistryTagKeyPredicate<T>(TagKey<T> key) implements RegistryPredicate<T> {
	@Override
	public boolean test(Holder<T> holder) {
		return holder.is(key);
	}

	@Override
	public String toString() {
		return "#" + key.location();
	}
}

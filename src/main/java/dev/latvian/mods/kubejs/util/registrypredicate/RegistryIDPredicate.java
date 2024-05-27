package dev.latvian.mods.kubejs.util.registrypredicate;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

public record RegistryIDPredicate<T>(ResourceLocation id) implements RegistryPredicate<T> {
	@Override
	public boolean test(Holder<T> holder) {
		return holder.is(id);
	}

	@Override
	public String toString() {
		return id.toString();
	}
}

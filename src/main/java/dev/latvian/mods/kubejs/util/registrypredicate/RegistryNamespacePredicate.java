package dev.latvian.mods.kubejs.util.registrypredicate;

import net.minecraft.core.Holder;

public record RegistryNamespacePredicate<T>(String namespace) implements RegistryPredicate<T> {
	@Override
	public boolean test(Holder<T> holder) {
		if (holder instanceof Holder.Reference<T> ref) {
			return ref.key().location().getNamespace().equals(namespace);
		} else {
			return holder.getKey().location().getNamespace().equals(namespace);
		}
	}

	@Override
	public String toString() {
		return "@" + namespace;
	}
}

package dev.latvian.mods.kubejs.util.registrypredicate;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.function.Predicate;

public record RegistryTagIDPredicate<T>(TagKeyPredicate predicate) implements RegistryPredicate<T> {
	private record TagKeyPredicate(ResourceLocation tag) implements Predicate<TagKey<?>> {
		@Override
		public boolean test(TagKey<?> key) {
			return key.location().equals(tag);
		}

		@Override
		public String toString() {
			return "#" + tag;
		}
	}

	public RegistryTagIDPredicate(ResourceLocation tag) {
		this(new TagKeyPredicate(tag));
	}

	@Override
	public boolean test(Holder<T> holder) {
		return holder.tags().anyMatch(predicate);
	}

	@Override
	public String toString() {
		return predicate.toString();
	}
}

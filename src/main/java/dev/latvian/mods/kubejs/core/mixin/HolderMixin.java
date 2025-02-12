package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Predicate;

@Mixin(Holder.class)
@RemapPrefixForJS("kjs$")
public interface HolderMixin<T> {
	@Shadow
	boolean is(TagKey<T> tagKey);

	@Shadow
	boolean is(ResourceKey<T> resourceKey);

	@Shadow
	boolean is(Predicate<ResourceKey<T>> predicate);

	@Unique
	default boolean kjs$matchTag(TagKey<T> tagKey) {
		return is(tagKey);
	}

	@Unique
	default boolean kjs$matchKey(ResourceKey<T> resourceKey) {
		return is(resourceKey);
	}

	@Unique
	default boolean kjs$matchPredicate(Predicate<ResourceKey<T>> predicate) {
		return is(predicate);
	}
}

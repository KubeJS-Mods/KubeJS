package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;
import java.util.function.Predicate;

@Mixin(Holder.class)
@RemapPrefixForJS("kjs$")
public interface HolderMixin<T> {
	@Shadow
	@HideFromJS
	boolean is(TagKey<T> tagKey);

	@Shadow
	@HideFromJS
	boolean is(ResourceKey<T> resourceKey);

	@Shadow
	@Deprecated
	@HideFromJS
	boolean is(Holder<T> holder);

	@Shadow
	@Info("Tests the resource key held by the holder using a predicate.")
	@RemapForJS("test")
	boolean is(Predicate<ResourceKey<T>> predicate);

	@Shadow
	Optional<ResourceKey<T>> unwrapKey();

	@Shadow
	@Info("Test if the holder holds an object registered with given resource location.")
	boolean is(ResourceLocation location);

	@Unique
	@Info("Test if a tag matches the object this holder holds.")
	default boolean kjs$isTag(ResourceLocation tagKey) {
		return this.unwrapKey().map(resourceKey -> is(TagKey.create(resourceKey.registryKey(), tagKey))).orElse(false);
	}
}

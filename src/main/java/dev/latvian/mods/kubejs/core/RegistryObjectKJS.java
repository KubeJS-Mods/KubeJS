package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.Collection;

@RemapPrefixForJS("kjs$")
public interface RegistryObjectKJS<T> {
	default ResourceKey<Registry<T>> kjs$getRegistryId() {
		throw new NoMixinException();
	}

	default Registry<T> kjs$getRegistry() {
		return RegistryAccessContainer.current.wrapRegistry(kjs$getRegistryId().location()).registry();
	}

	@SuppressWarnings("unchecked")
	default Holder<T> kjs$asHolder() {
		try {
			return kjs$getRegistry().wrapAsHolder((T) this);
		} catch (Exception ex) {
			return (Holder<T>) Holder.direct(this);
		}
	}

	default ResourceKey<T> kjs$getKey() {
		try {
			var h = kjs$asHolder();
			return h instanceof Holder.Reference ref ? ref.key() : h.unwrapKey().orElseThrow();
		} catch (Exception ex) {
			return kjs$getRegistry().getResourceKey((T) this).orElseThrow();
		}
	}

	default ResourceLocation kjs$getIdLocation() {
		return kjs$getKey().location();
	}

	default String kjs$getId() {
		return kjs$getIdLocation().toString();
	}

	default String kjs$getMod() {
		return kjs$getIdLocation().getNamespace();
	}

	default Collection<ResourceLocation> kjs$getTags() {
		return kjs$asHolder().tags().map(TagKey::location).toList();
	}

	default boolean kjs$hasTag(ResourceLocation tag) {
		return kjs$asHolder().is(TagKey.create(kjs$getRegistryId(), tag));
	}
}

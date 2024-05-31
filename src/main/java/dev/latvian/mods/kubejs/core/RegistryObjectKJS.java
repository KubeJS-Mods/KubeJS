package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.Collection;

@RemapPrefixForJS("kjs$")
public interface RegistryObjectKJS<T> {
	default RegistryInfo<T> kjs$getKubeRegistry() {
		throw new NoMixinException();
	}

	default Holder<T> kjs$asHolder() {
		return kjs$getKubeRegistry().getHolderOf(Cast.to(this));
	}

	default ResourceKey<T> kjs$getRegistryKey() {
		try {
			var h = kjs$asHolder();
			return h instanceof Holder.Reference ref ? ref.key() : h.unwrapKey().orElseThrow();
		} catch (Exception ex) {
			return kjs$getKubeRegistry().getKeyOf(Cast.to(this));
		}
	}

	default ResourceLocation kjs$getIdLocation() {
		return kjs$getRegistryKey().location();
	}

	default String kjs$getId() {
		return kjs$getIdLocation().toString();
	}

	default String kjs$getMod() {
		return kjs$getIdLocation().getNamespace();
	}

	default Collection<ResourceLocation> kjs$getTags(Context cx) {
		return kjs$asHolder().tags().map(TagKey::location).toList();
	}

	default boolean kjs$hasTag(ResourceLocation tag) {
		return kjs$asHolder().is(TagKey.create(kjs$getKubeRegistry().key, tag));
	}
}

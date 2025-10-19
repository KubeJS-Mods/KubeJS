package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import dev.latvian.mods.rhino.util.SpecialEquality;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.List;

@RemapPrefixForJS("kjs$")
public interface RegistryObjectKJS<T> extends SpecialEquality {
	@Override
	default boolean specialEquals(Context cx, Object o, boolean shallow) {
		return switch (o) {
			case CharSequence cs -> kjs$getId().equals(cs.toString());
			case ResourceLocation id -> kjs$getIdLocation().equals(id);
			case null, default -> equals(o);
		};

	}

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
			return kjs$asHolder().getKey();
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

	default List<TagKey<T>> kjs$getTagKeys() {
		return kjs$asHolder().tags().toList();
	}

	default List<ResourceLocation> kjs$getTags() {
		return kjs$asHolder().tags().map(TagKey::location).toList();
	}

	default boolean kjs$hasTag(ResourceLocation tag) {
		return kjs$asHolder().is(TagKey.create(kjs$getRegistryId(), tag));
	}
}

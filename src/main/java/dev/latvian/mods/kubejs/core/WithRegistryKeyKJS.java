package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

@RemapPrefixForJS("kjs$")
public interface WithRegistryKeyKJS<T> {
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
}

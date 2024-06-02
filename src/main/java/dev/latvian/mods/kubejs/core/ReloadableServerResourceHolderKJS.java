package dev.latvian.mods.kubejs.core;

public interface ReloadableServerResourceHolderKJS {
	default void kjs$setResources(ReloadableServerResourcesKJS resources) {
		throw new NoMixinException();
	}

	default ReloadableServerResourcesKJS kjs$getResources() {
		throw new NoMixinException();
	}
}
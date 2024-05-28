package dev.latvian.mods.kubejs.core;

public interface ReloadableServerResourceHolderKJS {
	void kjs$setResources(ReloadableServerResourcesKJS resources);

	ReloadableServerResourcesKJS kjs$getResources();
}
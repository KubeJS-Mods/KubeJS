package dev.latvian.mods.kubejs.core;

public interface TagManagerKJS {
	void kjs$setResources(ReloadableServerResourcesKJS resources);

	ReloadableServerResourcesKJS kjs$getResources();
}
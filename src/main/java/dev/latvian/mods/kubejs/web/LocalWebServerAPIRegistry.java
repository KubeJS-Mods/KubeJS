package dev.latvian.mods.kubejs.web;

import net.minecraft.resources.ResourceLocation;

public interface LocalWebServerAPIRegistry {
	void register(ResourceLocation id, int version);
}

package dev.latvian.mods.kubejs.web;


import net.minecraft.resources.Identifier;

public interface LocalWebServerAPIRegistry {
	void register(Identifier id, int version);
}

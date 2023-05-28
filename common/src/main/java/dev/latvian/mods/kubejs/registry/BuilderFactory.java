package dev.latvian.mods.kubejs.registry;

import net.minecraft.resources.ResourceLocation;

public interface BuilderFactory {
	BuilderBase createBuilder(ResourceLocation id);
}
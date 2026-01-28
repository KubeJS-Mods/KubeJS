package dev.latvian.mods.kubejs.registry;

import net.minecraft.resources.Identifier;

public interface BuilderFactory {
	BuilderBase createBuilder(Identifier id);
}
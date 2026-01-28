package dev.latvian.mods.kubejs.registry;

import net.minecraft.resources.Identifier;

public record BuilderType<T>(Identifier type, Class<? extends BuilderBase<? extends T>> builderClass, BuilderFactory factory) {
}
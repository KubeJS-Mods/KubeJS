package dev.latvian.mods.kubejs.registry;

import net.minecraft.resources.ResourceLocation;

public record BuilderType<T>(ResourceLocation type, Class<? extends BuilderBase<? extends T>> builderClass, BuilderFactory factory) {
}
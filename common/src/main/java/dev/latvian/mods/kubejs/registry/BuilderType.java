package dev.latvian.mods.kubejs.registry;

public record BuilderType<T>(String type, Class<? extends BuilderBase<? extends T>> builderClass, BuilderFactory factory) {
}
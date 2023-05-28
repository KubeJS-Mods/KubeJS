package dev.latvian.mods.kubejs.registry;

public record BuilderType(String type, Class<? extends BuilderBase> builderClass, BuilderFactory factory) {
}
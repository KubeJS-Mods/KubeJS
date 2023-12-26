package dev.latvian.mods.kubejs.registry;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public interface RegistryCallback<T> {
	void accept(ResourceLocation id, Supplier<T> obj);
}

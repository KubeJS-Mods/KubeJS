package dev.latvian.mods.kubejs.registry;

import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

public interface RegistryCallback<T> {
	void accept(Identifier id, Supplier<T> obj);
}

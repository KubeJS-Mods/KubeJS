package dev.latvian.mods.kubejs.core;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public interface RegistryGetterKJS<E> extends Supplier<E> {
	Registry<E> getRegistry();

	ResourceLocation getId();
}

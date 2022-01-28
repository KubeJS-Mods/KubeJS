package dev.latvian.mods.kubejs.core;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public interface RegistryGetterKJS<E> {
	Registry<E> getRegistry();

	ResourceLocation getId();
}

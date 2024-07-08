package dev.latvian.mods.kubejs.core;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;

public interface EntityTypeKJS extends RegistryObjectKJS<EntityType<?>> {
	@Override
	default ResourceKey<Registry<EntityType<?>>> kjs$getRegistryId() {
		return Registries.ENTITY_TYPE;
	}

	@Override
	default Registry<EntityType<?>> kjs$getRegistry() {
		return BuiltInRegistries.ENTITY_TYPE;
	}
}

package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.world.entity.EntityType;

public interface EntityTypeKJS extends WithRegistryKeyKJS<EntityType<?>> {
	@Override
	default RegistryInfo kjs$getKubeRegistry() {
		return RegistryInfo.ENTITY_TYPE;
	}
}

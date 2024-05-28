package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.entity.CheckLivingEntitySpawnKubeEvent;
import dev.latvian.mods.kubejs.entity.EntitySpawnedKubeEvent;
import dev.latvian.mods.kubejs.entity.LivingEntityDeathKubeEvent;
import dev.latvian.mods.kubejs.entity.LivingEntityDropsKubeEvent;
import dev.latvian.mods.kubejs.entity.LivingEntityHurtKubeEvent;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.event.SpecializedEventHandler;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;

public interface EntityEvents {
	EventGroup GROUP = EventGroup.of("EntityEvents");
	Extra<ResourceKey<EntityType<?>>> SUPPORTS_ENTITY_TYPE = Extra.registryKey(Registries.ENTITY_TYPE, EntityType.class);

	SpecializedEventHandler<ResourceKey<EntityType<?>>> DEATH = GROUP.common("death", SUPPORTS_ENTITY_TYPE, () -> LivingEntityDeathKubeEvent.class).hasResult();
	SpecializedEventHandler<ResourceKey<EntityType<?>>> HURT = GROUP.common("hurt", SUPPORTS_ENTITY_TYPE, () -> LivingEntityHurtKubeEvent.class).hasResult();
	SpecializedEventHandler<ResourceKey<EntityType<?>>> CHECK_SPAWN = GROUP.common("checkSpawn", SUPPORTS_ENTITY_TYPE, () -> CheckLivingEntitySpawnKubeEvent.class).hasResult();
	SpecializedEventHandler<ResourceKey<EntityType<?>>> SPAWNED = GROUP.common("spawned", SUPPORTS_ENTITY_TYPE, () -> EntitySpawnedKubeEvent.class).hasResult();
	SpecializedEventHandler<ResourceKey<EntityType<?>>> ENTITY_DROPS = GROUP.common("drops", SUPPORTS_ENTITY_TYPE, () -> LivingEntityDropsKubeEvent.class).hasResult();
}

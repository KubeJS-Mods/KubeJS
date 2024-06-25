package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.entity.AfterLivingEntityHurtKubeEvent;
import dev.latvian.mods.kubejs.entity.BeforeLivingEntityHurtKubeEvent;
import dev.latvian.mods.kubejs.entity.CheckLivingEntitySpawnKubeEvent;
import dev.latvian.mods.kubejs.entity.EntitySpawnedKubeEvent;
import dev.latvian.mods.kubejs.entity.LivingEntityDeathKubeEvent;
import dev.latvian.mods.kubejs.entity.LivingEntityDropsKubeEvent;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventTargetType;
import dev.latvian.mods.kubejs.event.TargetedEventHandler;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;

public interface EntityEvents {
	EventGroup GROUP = EventGroup.of("EntityEvents");
	EventTargetType<ResourceKey<EntityType<?>>> TARGET = EventTargetType.registryKey(Registries.ENTITY_TYPE, EntityType.class);

	TargetedEventHandler<ResourceKey<EntityType<?>>> DEATH = GROUP.common("death", () -> LivingEntityDeathKubeEvent.class).hasResult().supportsTarget(TARGET);
	TargetedEventHandler<ResourceKey<EntityType<?>>> BEFORE_HURT = GROUP.common("beforeHurt", () -> BeforeLivingEntityHurtKubeEvent.class).hasResult().supportsTarget(TARGET);
	TargetedEventHandler<ResourceKey<EntityType<?>>> AFTER_HURT = GROUP.common("afterHurt", () -> AfterLivingEntityHurtKubeEvent.class).supportsTarget(TARGET);
	TargetedEventHandler<ResourceKey<EntityType<?>>> CHECK_SPAWN = GROUP.common("checkSpawn", () -> CheckLivingEntitySpawnKubeEvent.class).hasResult().supportsTarget(TARGET);
	TargetedEventHandler<ResourceKey<EntityType<?>>> SPAWNED = GROUP.common("spawned", () -> EntitySpawnedKubeEvent.class).hasResult().supportsTarget(TARGET);
	TargetedEventHandler<ResourceKey<EntityType<?>>> ENTITY_DROPS = GROUP.common("drops", () -> LivingEntityDropsKubeEvent.class).hasResult().supportsTarget(TARGET);
}

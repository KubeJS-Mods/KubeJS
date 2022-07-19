package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.entity.CheckLivingEntitySpawnEventJS;
import dev.latvian.mods.kubejs.entity.EntitySpawnedEventJS;
import dev.latvian.mods.kubejs.entity.LivingEntityDeathEventJS;
import dev.latvian.mods.kubejs.entity.LivingEntityHurtEventJS;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

public interface EntityEvents {
	EventGroup GROUP = EventGroup.of("EntityEvents");
	EventHandler DEATH = GROUP.server("death", () -> LivingEntityDeathEventJS.class).supportsNamespacedExtraId().cancelable().legacy("entity.death");
	EventHandler HURT = GROUP.server("hurt", () -> LivingEntityHurtEventJS.class).supportsNamespacedExtraId().cancelable().legacy("entity.hurt");
	EventHandler CHECK_SPAWN = GROUP.server("checkSpawn", () -> CheckLivingEntitySpawnEventJS.class).supportsNamespacedExtraId().cancelable().legacy("entity.check_spawn");
	EventHandler SPAWNED = GROUP.server("spawned", () -> EntitySpawnedEventJS.class).supportsNamespacedExtraId().cancelable().legacy("entity.spawned");
}

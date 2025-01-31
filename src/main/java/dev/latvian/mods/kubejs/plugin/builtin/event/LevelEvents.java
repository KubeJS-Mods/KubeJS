package dev.latvian.mods.kubejs.plugin.builtin.event;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventTargetType;
import dev.latvian.mods.kubejs.event.TargetedEventHandler;
import dev.latvian.mods.kubejs.level.ExplosionKubeEvent;
import dev.latvian.mods.kubejs.level.SimpleLevelKubeEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public interface LevelEvents {
	EventGroup GROUP = EventGroup.of("LevelEvents");
	EventTargetType<ResourceKey<Level>> TARGET = EventTargetType.registryKey(Registries.DIMENSION, Level.class);

	TargetedEventHandler<ResourceKey<Level>> LOADED = GROUP.server("loaded", () -> SimpleLevelKubeEvent.class).supportsTarget(TARGET);
	TargetedEventHandler<ResourceKey<Level>> SAVED = GROUP.server("saved", () -> SimpleLevelKubeEvent.class).supportsTarget(TARGET);
	TargetedEventHandler<ResourceKey<Level>> UNLOADED = GROUP.server("unloaded", () -> SimpleLevelKubeEvent.class).supportsTarget(TARGET);
	TargetedEventHandler<ResourceKey<Level>> TICK = GROUP.common("tick", () -> SimpleLevelKubeEvent.class).supportsTarget(TARGET);
	TargetedEventHandler<ResourceKey<Level>> BEFORE_EXPLOSION = GROUP.common("beforeExplosion", () -> ExplosionKubeEvent.Before.class).hasResult().supportsTarget(TARGET);
	TargetedEventHandler<ResourceKey<Level>> AFTER_EXPLOSION = GROUP.common("afterExplosion", () -> ExplosionKubeEvent.After.class).supportsTarget(TARGET);
}

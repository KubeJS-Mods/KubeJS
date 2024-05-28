package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.event.SpecializedEventHandler;
import dev.latvian.mods.kubejs.level.ExplosionKubeEvent;
import dev.latvian.mods.kubejs.level.SimpleLevelKubeEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public interface LevelEvents {
	EventGroup GROUP = EventGroup.of("LevelEvents");
	Extra<ResourceKey<Level>> SUPPORTS_LEVEL = Extra.registryKey(Registries.DIMENSION, Level.class);

	SpecializedEventHandler<ResourceKey<Level>> LOADED = GROUP.server("loaded", SUPPORTS_LEVEL, () -> SimpleLevelKubeEvent.class);
	SpecializedEventHandler<ResourceKey<Level>> SAVED = GROUP.server("saved", SUPPORTS_LEVEL, () -> SimpleLevelKubeEvent.class);
	SpecializedEventHandler<ResourceKey<Level>> UNLOADED = GROUP.server("unloaded", SUPPORTS_LEVEL, () -> SimpleLevelKubeEvent.class);
	SpecializedEventHandler<ResourceKey<Level>> TICK = GROUP.common("tick", SUPPORTS_LEVEL, () -> SimpleLevelKubeEvent.class);
	SpecializedEventHandler<ResourceKey<Level>> BEFORE_EXPLOSION = GROUP.common("beforeExplosion", SUPPORTS_LEVEL, () -> ExplosionKubeEvent.Before.class).hasResult();
	SpecializedEventHandler<ResourceKey<Level>> AFTER_EXPLOSION = GROUP.common("afterExplosion", SUPPORTS_LEVEL, () -> ExplosionKubeEvent.After.class);
}

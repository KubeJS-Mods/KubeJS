package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.level.ExplosionKubeEvent;
import dev.latvian.mods.kubejs.level.SimpleLevelKubeEvent;

public interface LevelEvents {
	EventGroup GROUP = EventGroup.of("LevelEvents");
	EventHandler LOADED = GROUP.server("loaded", () -> SimpleLevelKubeEvent.class).extra(Extra.ID);
	EventHandler UNLOADED = GROUP.server("unloaded", () -> SimpleLevelKubeEvent.class).extra(Extra.ID);
	EventHandler TICK = GROUP.common("tick", () -> SimpleLevelKubeEvent.class).extra(Extra.ID);
	EventHandler BEFORE_EXPLOSION = GROUP.common("beforeExplosion", () -> ExplosionKubeEvent.Before.class).hasResult();
	EventHandler AFTER_EXPLOSION = GROUP.common("afterExplosion", () -> ExplosionKubeEvent.After.class);
}

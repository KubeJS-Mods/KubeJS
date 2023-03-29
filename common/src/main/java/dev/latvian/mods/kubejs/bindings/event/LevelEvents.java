package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.level.ExplosionEventJS;
import dev.latvian.mods.kubejs.level.SimpleLevelEventJS;

public interface LevelEvents {
	EventGroup GROUP = EventGroup.of("LevelEvents");
	EventHandler LOADED = GROUP.server("loaded", () -> SimpleLevelEventJS.class).extra(Extra.ID);
	EventHandler UNLOADED = GROUP.server("unloaded", () -> SimpleLevelEventJS.class).extra(Extra.ID);
	EventHandler TICK = GROUP.common("tick", () -> SimpleLevelEventJS.class).extra(Extra.ID);
	EventHandler BEFORE_EXPLOSION = GROUP.common("beforeExplosion", () -> ExplosionEventJS.Before.class).hasResult();
	EventHandler AFTER_EXPLOSION = GROUP.common("afterExplosion", () -> ExplosionEventJS.After.class);
}

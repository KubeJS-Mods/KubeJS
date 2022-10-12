package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.level.ExplosionEventJS;
import dev.latvian.mods.kubejs.level.SimpleLevelEventJS;

public interface LevelEvents {
	EventGroup GROUP = EventGroup.of("LevelEvents");
	EventHandler LOADED = GROUP.server("loaded", () -> SimpleLevelEventJS.class).supportsNamespacedExtraId();
	EventHandler UNLOADED = GROUP.server("unloaded", () -> SimpleLevelEventJS.class).supportsNamespacedExtraId();
	EventHandler TICK = GROUP.server("tick", () -> SimpleLevelEventJS.class).supportsNamespacedExtraId();
	EventHandler BEFORE_EXPLOSION = GROUP.server("beforeExplosion", () -> ExplosionEventJS.Before.class).cancelable();
	EventHandler AFTER_EXPLOSION = GROUP.server("afterExplosion", () -> ExplosionEventJS.After.class);
}

package dev.latvian.mods.kubejs.level;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.ExplosionEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.latvian.mods.kubejs.bindings.event.LevelEvents;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * @author LatvianModder
 */
public class KubeJSWorldEventHandler {
	public static void init() {
		LifecycleEvent.SERVER_LEVEL_LOAD.register(KubeJSWorldEventHandler::levelLoad);
		LifecycleEvent.SERVER_LEVEL_UNLOAD.register(KubeJSWorldEventHandler::levelUnload);
		TickEvent.SERVER_LEVEL_POST.register(KubeJSWorldEventHandler::levelPostTick);
		ExplosionEvent.PRE.register(KubeJSWorldEventHandler::preExplosion);
		ExplosionEvent.DETONATE.register(KubeJSWorldEventHandler::detonateExplosion);
	}

	private static void levelLoad(ServerLevel level) {
		if (LevelEvents.LOADED.hasListeners()) {
			LevelEvents.LOADED.post(ScriptType.SERVER, level.dimension().location(), new SimpleLevelEventJS(level));
		}
	}

	private static void levelUnload(ServerLevel level) {
		if (LevelEvents.UNLOADED.hasListeners()) {
			LevelEvents.UNLOADED.post(ScriptType.SERVER, level.dimension().location(), new SimpleLevelEventJS(level));
		}
	}

	private static void levelPostTick(ServerLevel level) {
		if (LevelEvents.TICK.hasListeners()) {
			LevelEvents.TICK.post(ScriptType.SERVER, level.dimension().location(), new SimpleLevelEventJS(level));
		}
	}

	private static EventResult preExplosion(Level level, Explosion explosion) {
		return LevelEvents.BEFORE_EXPLOSION.hasListeners() ? LevelEvents.BEFORE_EXPLOSION.post(ScriptType.of(level), new ExplosionEventJS.Before(level, explosion)).arch() : EventResult.pass();
	}

	private static void detonateExplosion(Level level, Explosion explosion, List<Entity> affectedEntities) {
		if (LevelEvents.AFTER_EXPLOSION.hasListeners()) {
			LevelEvents.AFTER_EXPLOSION.post(ScriptType.of(level), new ExplosionEventJS.After(level, explosion, affectedEntities));
		}
	}
}
package dev.latvian.mods.kubejs.level;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.ExplosionEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.latvian.mods.kubejs.bindings.event.LevelEvents;
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
		LevelEvents.LOADED.post(level.dimension().location(), new SimpleLevelEventJS(level));
	}

	private static void levelUnload(ServerLevel level) {
		LevelEvents.UNLOADED.post(level.dimension().location(), new SimpleLevelEventJS(level));
	}

	private static void levelPostTick(ServerLevel level) {
		LevelEvents.TICK.post(level.dimension().location(), new SimpleLevelEventJS(level));
	}

	private static EventResult preExplosion(Level level, Explosion explosion) {
		if (LevelEvents.BEFORE_EXPLOSION.post(new ExplosionEventJS.Before(level, explosion))) {
			return EventResult.interruptFalse();
		}

		return EventResult.pass();
	}

	private static void detonateExplosion(Level level, Explosion explosion, List<Entity> affectedEntities) {
		LevelEvents.AFTER_EXPLOSION.post(new ExplosionEventJS.After(level, explosion, affectedEntities));
	}
}
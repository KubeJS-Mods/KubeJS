package dev.latvian.mods.kubejs.level;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.ExplosionEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.latvian.mods.kubejs.script.AttachDataEvent;
import dev.latvian.mods.kubejs.server.ServerJS;
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
		if (ServerJS.instance != null && ServerJS.instance.overworld != null && !ServerJS.instance.levelMap.containsKey(level.dimension().location())) {
			var l = new ServerLevelJS(ServerJS.instance, level);
			ServerJS.instance.levelMap.put(level.dimension().location(), l);
			ServerJS.instance.updateWorldList();
			AttachDataEvent.forLevel(l).invoke();
			SimpleLevelEventJS.LOAD_EVENT.post(new SimpleLevelEventJS(l));
		}
	}

	private static void levelUnload(ServerLevel level) {
		if (ServerJS.instance != null && ServerJS.instance.overworld != null && ServerJS.instance.levelMap.containsKey(level.dimension().location())) {
			var l = ServerJS.instance.wrapMinecraftLevel(level);
			SimpleLevelEventJS.UNLOAD_EVENT.post(new SimpleLevelEventJS(l));
			ServerJS.instance.levelMap.remove(l.getDimension());
			ServerJS.instance.updateWorldList();
		}
	}

	private static void levelPostTick(ServerLevel level) {
		var l = ServerJS.instance.wrapMinecraftLevel(level);
		SimpleLevelEventJS.TICK_EVENT.post(new SimpleLevelEventJS(l));
	}

	private static EventResult preExplosion(Level level, Explosion explosion) {
		if (ExplosionEventJS.PRE_EVENT.post(new ExplosionEventJS.Pre(level, explosion))) {
			return EventResult.interruptFalse();
		}

		return EventResult.pass();
	}

	private static void detonateExplosion(Level level, Explosion explosion, List<Entity> affectedEntities) {
		ExplosionEventJS.POST_EVENT.post(new ExplosionEventJS.Post(level, explosion, affectedEntities));
	}
}
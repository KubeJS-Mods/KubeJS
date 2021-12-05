package dev.latvian.mods.kubejs.world;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.ExplosionEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.latvian.mods.kubejs.KubeJSEvents;
import dev.latvian.mods.kubejs.script.ScriptType;
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
		if (ServerJS.instance != null && ServerJS.instance.overworld != null && !ServerJS.instance.levelMap.containsKey(level.dimension().location().toString())) {
			ServerWorldJS l = new ServerWorldJS(ServerJS.instance, level);
			ServerJS.instance.levelMap.put(level.dimension().location().toString(), l);
			ServerJS.instance.updateWorldList();
			new AttachWorldDataEvent(l).invoke();
			new SimpleWorldEventJS(l).post(ScriptType.SERVER, KubeJSEvents.WORLD_LOAD);
		}
	}

	private static void levelUnload(ServerLevel level) {
		if (ServerJS.instance != null && ServerJS.instance.overworld != null && ServerJS.instance.levelMap.containsKey(level.dimension().location().toString())) {
			WorldJS l = ServerJS.instance.getLevel(level);
			new SimpleWorldEventJS(l).post(ScriptType.SERVER, KubeJSEvents.WORLD_UNLOAD);
			ServerJS.instance.levelMap.remove(l.getDimension());
			ServerJS.instance.updateWorldList();
		}
	}

	private static void levelPostTick(ServerLevel level) {
		WorldJS l = ServerJS.instance.getLevel(level);
		new SimpleWorldEventJS(l).post(ScriptType.SERVER, KubeJSEvents.WORLD_TICK);
	}

	private static EventResult preExplosion(Level level, Explosion explosion) {
		if (new ExplosionEventJS.Pre(level, explosion).post(KubeJSEvents.WORLD_EXPLOSION_PRE)) {
			return EventResult.interruptFalse();
		}
		return EventResult.pass();
	}

	private static void detonateExplosion(Level level, Explosion explosion, List<Entity> affectedEntities) {
		new ExplosionEventJS.Post(level, explosion, affectedEntities).post(KubeJSEvents.WORLD_EXPLOSION_POST);
	}
}
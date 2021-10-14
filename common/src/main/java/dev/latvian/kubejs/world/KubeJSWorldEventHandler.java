package dev.latvian.kubejs.world;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.ExplosionEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerJS;
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
		LifecycleEvent.SERVER_LEVEL_LOAD.register(KubeJSWorldEventHandler::worldLoaded);
		LifecycleEvent.SERVER_LEVEL_UNLOAD.register(KubeJSWorldEventHandler::worldUnloaded);
		TickEvent.SERVER_LEVEL_POST.register(KubeJSWorldEventHandler::worldTick);
		ExplosionEvent.PRE.register(KubeJSWorldEventHandler::explosionStart);
		ExplosionEvent.DETONATE.register(KubeJSWorldEventHandler::explosionDetonate);
	}

	private static void worldLoaded(ServerLevel level) {
		if (ServerJS.instance != null && ServerJS.instance.overworld != null && !ServerJS.instance.levelMap.containsKey(level.dimension().location().toString())) {
			ServerWorldJS w = new ServerWorldJS(ServerJS.instance, level);
			ServerJS.instance.levelMap.put(level.dimension().location().toString(), w);
			ServerJS.instance.updateWorldList();
			new AttachWorldDataEvent(w).invoke();
			new SimpleWorldEventJS(w).post(ScriptType.SERVER, KubeJSEvents.WORLD_LOAD);
		}
	}

	private static void worldUnloaded(ServerLevel level) {
		if (ServerJS.instance != null && ServerJS.instance.overworld != null && ServerJS.instance.levelMap.containsKey(level.dimension().location().toString())) {
			WorldJS w = ServerJS.instance.getLevel(level);
			new SimpleWorldEventJS(w).post(ScriptType.SERVER, KubeJSEvents.WORLD_UNLOAD);
			ServerJS.instance.levelMap.remove(w.getDimension());
			ServerJS.instance.updateWorldList();
		}
	}

	private static void worldTick(ServerLevel level) {
		WorldJS w = ServerJS.instance.getLevel(level);
		new SimpleWorldEventJS(w).post(ScriptType.SERVER, KubeJSEvents.WORLD_TICK);
	}

	private static EventResult explosionStart(Level world, Explosion explosion) {
		if (new ExplosionEventJS.Pre(world, explosion).post(KubeJSEvents.WORLD_EXPLOSION_PRE)) {
			return EventResult.interruptFalse();
		}
		return EventResult.pass();
	}

	private static void explosionDetonate(Level world, Explosion explosion, List<Entity> affectedEntities) {
		new ExplosionEventJS.Post(world, explosion, affectedEntities).post(KubeJSEvents.WORLD_EXPLOSION_POST);
	}
}
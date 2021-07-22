package dev.latvian.kubejs.world;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerJS;
import me.shedaniel.architectury.event.events.ExplosionEvent;
import me.shedaniel.architectury.event.events.LifecycleEvent;
import me.shedaniel.architectury.event.events.TickEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * @author LatvianModder
 */
public class KubeJSWorldEventHandler {
	public static void init() {
		LifecycleEvent.SERVER_WORLD_LOAD.register(KubeJSWorldEventHandler::worldLoaded);
		LifecycleEvent.SERVER_WORLD_UNLOAD.register(KubeJSWorldEventHandler::worldUnloaded);
		TickEvent.SERVER_WORLD_POST.register(KubeJSWorldEventHandler::worldTick);
		ExplosionEvent.PRE.register(KubeJSWorldEventHandler::explosionStart);
		ExplosionEvent.DETONATE.register(KubeJSWorldEventHandler::explosionDetonate);
	}

	private static void worldLoaded(ServerLevel level) {
		if (ServerJS.instance != null && ServerJS.instance.overworld != null && !ServerJS.instance.worldMap.containsKey(level.dimension().location().toString())) {
			ServerWorldJS w = new ServerWorldJS(ServerJS.instance, level);
			ServerJS.instance.worldMap.put(level.dimension().location().toString(), w);
			ServerJS.instance.updateWorldList();
			new AttachWorldDataEvent(w).invoke();
			new SimpleWorldEventJS(w).post(ScriptType.SERVER, KubeJSEvents.WORLD_LOAD);
		}
	}

	private static void worldUnloaded(ServerLevel level) {
		if (ServerJS.instance != null && ServerJS.instance.overworld != null && ServerJS.instance.worldMap.containsKey(level.dimension().location().toString())) {
			WorldJS w = ServerJS.instance.getWorld(level);
			new SimpleWorldEventJS(w).post(ScriptType.SERVER, KubeJSEvents.WORLD_UNLOAD);
			ServerJS.instance.worldMap.remove(w.getDimension());
			ServerJS.instance.updateWorldList();
		}
	}

	private static void worldTick(ServerLevel level) {
		WorldJS w = ServerJS.instance.getWorld(level);
		new SimpleWorldEventJS(w).post(ScriptType.SERVER, KubeJSEvents.WORLD_TICK);
	}

	private static InteractionResult explosionStart(Level world, Explosion explosion) {
		if (new ExplosionEventJS.Pre(world, explosion).post(KubeJSEvents.WORLD_EXPLOSION_PRE)) {
			return InteractionResult.FAIL;
		}
		return InteractionResult.PASS;
	}

	private static void explosionDetonate(Level world, Explosion explosion, List<Entity> affectedEntities) {
		new ExplosionEventJS.Post(world, explosion, affectedEntities).post(KubeJSEvents.WORLD_EXPLOSION_POST);
	}
}
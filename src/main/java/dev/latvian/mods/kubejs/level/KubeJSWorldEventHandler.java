package dev.latvian.mods.kubejs.level;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.bindings.event.LevelEvents;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = KubeJS.MOD_ID)
public class KubeJSWorldEventHandler {
	@SubscribeEvent
	public static void serverLevelLoad(LevelEvent.Load event) {
		if (event.getLevel() instanceof ServerLevel level && LevelEvents.LOADED.hasListeners(level.dimension())) {
			LevelEvents.LOADED.post(new SimpleLevelKubeEvent(level), level.dimension());
		}
	}

	@SubscribeEvent
	public static void serverLevelUnload(LevelEvent.Unload event) {
		if (event.getLevel() instanceof ServerLevel level && LevelEvents.UNLOADED.hasListeners(level.dimension())) {
			LevelEvents.UNLOADED.post(new SimpleLevelKubeEvent(level), level.dimension());
		}
	}

	@SubscribeEvent
	public static void serverTickEvent(LevelTickEvent.Post event) {
		if (event.getLevel() instanceof ServerLevel level && LevelEvents.TICK.hasListeners(level.dimension())) {
			LevelEvents.TICK.post(ScriptType.SERVER, level.dimension(), new SimpleLevelKubeEvent(level));
		}
	}

	@SubscribeEvent
	public static void preExplosion(ExplosionEvent.Start event) {
		if (event.getLevel() instanceof ServerLevel level && LevelEvents.BEFORE_EXPLOSION.hasListeners(level.dimension())) {
			LevelEvents.BEFORE_EXPLOSION.post(level, level.dimension(), new ExplosionKubeEvent.Before(level, event.getExplosion())).applyCancel(event);
		}
	}

	@SubscribeEvent
	public static void detonateExplosion(ExplosionEvent.Detonate event) {
		if (event.getLevel() instanceof ServerLevel level && LevelEvents.AFTER_EXPLOSION.hasListeners(level.dimension())) {
			LevelEvents.AFTER_EXPLOSION.post(level, level.dimension(), new ExplosionKubeEvent.After(level, event.getExplosion(), event.getAffectedEntities()));
		}
	}
}
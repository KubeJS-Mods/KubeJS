package dev.latvian.mods.kubejs.entity;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.latvian.mods.kubejs.bindings.event.EntityEvents;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

/**
 * @author LatvianModder
 */
public class KubeJSEntityEventHandler {
	public static void init() {
		EntityEvent.LIVING_CHECK_SPAWN.register(KubeJSEntityEventHandler::checkSpawn);
		EntityEvent.LIVING_DEATH.register(KubeJSEntityEventHandler::livingDeath);
		EntityEvent.LIVING_HURT.register(KubeJSEntityEventHandler::livingHurt);
		EntityEvent.ADD.register(KubeJSEntityEventHandler::entitySpawned);
	}

	private static EventResult checkSpawn(LivingEntity entity, LevelAccessor la, double x, double y, double z, MobSpawnType type, BaseSpawner spawner) {
		if (la instanceof Level level && (la.isClientSide() || UtilsJS.staticServer != null) && EntityEvents.CHECK_SPAWN.hasListeners()) {
			return EntityEvents.CHECK_SPAWN.post(ScriptType.of(level), entity.getType(), new CheckLivingEntitySpawnEventJS(entity, level, x, y, z, type)).arch();
		}

		return EventResult.pass();
	}

	private static EventResult livingDeath(LivingEntity entity, DamageSource source) {
		return EntityEvents.DEATH.hasListeners() ? EntityEvents.DEATH.post(ScriptType.of(entity), entity.getType(), new LivingEntityDeathEventJS(entity, source)).arch() : EventResult.pass();
	}

	private static EventResult livingHurt(LivingEntity entity, DamageSource source, float amount) {
		return EntityEvents.HURT.hasListeners() ? EntityEvents.HURT.post(ScriptType.of(entity), entity.getType(), new LivingEntityHurtEventJS(entity, source, amount)).arch() : EventResult.pass();
	}

	private static EventResult entitySpawned(Entity entity, Level level) {
		if ((level.isClientSide() || UtilsJS.staticServer != null) && EntityEvents.SPAWNED.hasListeners()) {
			return EntityEvents.SPAWNED.post(ScriptType.of(level), entity.getType(), new EntitySpawnedEventJS(entity, level)).arch();
		}

		return EventResult.pass();
	}
}
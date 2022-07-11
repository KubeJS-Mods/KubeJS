package dev.latvian.mods.kubejs.entity;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.latvian.mods.kubejs.KubeJSEvents;
import dev.latvian.mods.kubejs.KubeJSRegistries;
import dev.latvian.mods.kubejs.server.ServerJS;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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

	private static String getTypeId(EntityType<?> type) {
		return String.valueOf(KubeJSRegistries.entityTypes().getId(type));
	}

	private static EventResult checkSpawn(LivingEntity entity, LevelAccessor la, double x, double y, double z, MobSpawnType type, BaseSpawner spawner) {
		if (ServerJS.instance != null && ServerJS.instance.overworld != null && !la.isClientSide() && la instanceof Level level &&
				KubeJSEvents.ENTITY_CHECK_SPAWN.post(getTypeId(entity.getType()), new CheckLivingEntitySpawnEventJS(entity, level, x, y, z, type))) {
			return EventResult.interruptFalse();
		}
		return EventResult.pass();
	}

	private static EventResult livingDeath(LivingEntity entity, DamageSource source) {
		if (entity != null && entity.level instanceof ServerLevel && KubeJSEvents.ENTITY_DEATH.post(getTypeId(entity.getType()), new LivingEntityDeathEventJS(entity, source))) {
			return EventResult.interruptFalse();
		}

		return EventResult.pass();
	}

	private static EventResult livingHurt(LivingEntity entity, DamageSource source, float amount) {
		if (entity != null && entity.level instanceof ServerLevel && amount > 0F && KubeJSEvents.ENTITY_HURT.post(getTypeId(entity.getType()), new LivingEntityHurtEventJS(entity, source, amount))) {
			return EventResult.interruptFalse();
		}
		return EventResult.pass();
	}

	private static EventResult entitySpawned(Entity entity, Level level) {
		if (entity != null && ServerJS.instance != null && ServerJS.instance.overworld != null && !level.isClientSide() && KubeJSEvents.ENTITY_SPAWNED.post(getTypeId(entity.getType()), new EntitySpawnedEventJS(entity, level))) {
			return EventResult.interruptFalse();
		}
		return EventResult.pass();
	}
}
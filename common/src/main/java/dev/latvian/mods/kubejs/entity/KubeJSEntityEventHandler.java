package dev.latvian.mods.kubejs.entity;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
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
				CheckLivingEntitySpawnEventJS.EVENT.post(new CheckLivingEntitySpawnEventJS(entity, level, x, y, z, type), getTypeId(entity.getType()))) {
			return EventResult.interruptFalse();
		}
		return EventResult.pass();
	}

	private static EventResult livingDeath(LivingEntity entity, DamageSource source) {
		if (entity != null && entity.level instanceof ServerLevel && LivingEntityDeathEventJS.EVENT.post(new LivingEntityDeathEventJS(entity, source), getTypeId(entity.getType()))) {
			return EventResult.interruptFalse();
		}

		return EventResult.pass();
	}

	private static EventResult livingHurt(LivingEntity entity, DamageSource source, float amount) {
		if (entity != null && entity.level instanceof ServerLevel && amount > 0F && LivingEntityHurtEventJS.EVENT.post(new LivingEntityHurtEventJS(entity, source, amount), getTypeId(entity.getType()))) {
			return EventResult.interruptFalse();
		}
		return EventResult.pass();
	}

	private static EventResult entitySpawned(Entity entity, Level level) {
		if (entity != null && ServerJS.instance != null && ServerJS.instance.overworld != null && !level.isClientSide() && EntitySpawnedEventJS.EVENT.post(new EntitySpawnedEventJS(entity, level), getTypeId(entity.getType()))) {
			return EventResult.interruptFalse();
		}
		return EventResult.pass();
	}
}
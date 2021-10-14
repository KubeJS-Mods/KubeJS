package dev.latvian.kubejs.entity;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerJS;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

/**
 * @author LatvianModder
 */
public class KubeJSEntityEventHandler {
	public static void init() {
		EntityEvent.LIVING_DEATH.register(KubeJSEntityEventHandler::livingDeath);
		EntityEvent.LIVING_HURT.register(KubeJSEntityEventHandler::livingHurt);
		EntityEvent.ADD.register(KubeJSEntityEventHandler::entitySpawned);
	}

	private static EventResult livingDeath(LivingEntity entity, DamageSource source) {
		if (entity != null && new LivingEntityDeathEventJS(entity, source).post(KubeJSEvents.ENTITY_DEATH)) {
			return EventResult.interruptFalse();
		}
		return EventResult.pass();
	}

	private static EventResult livingHurt(LivingEntity entity, DamageSource source, float amount) {
		if (entity != null && amount > 0F && new LivingEntityHurtEventJS(entity, source, amount).post(KubeJSEvents.ENTITY_HURT)) {
			return EventResult.interruptFalse();
		}
		return EventResult.pass();
	}

	private static EventResult entitySpawned(Entity entity, Level world) {
		if (entity != null && ServerJS.instance != null && ServerJS.instance.overworld != null && !world.isClientSide() && new EntitySpawnedEventJS(entity, world).post(ScriptType.SERVER, KubeJSEvents.ENTITY_SPAWNED)) {
			return EventResult.interruptFalse();
		}
		return EventResult.pass();
	}
}
package dev.latvian.mods.kubejs.entity;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.latvian.mods.kubejs.KubeJSEvents;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.server.ServerJS;
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

	private static EventResult entitySpawned(Entity entity, Level level) {
		if (entity != null && ServerJS.instance != null && ServerJS.instance.overworld != null && !level.isClientSide() && new EntitySpawnedEventJS(entity, level).post(ScriptType.SERVER, KubeJSEvents.ENTITY_SPAWNED)) {
			return EventResult.interruptFalse();
		}
		return EventResult.pass();
	}
}
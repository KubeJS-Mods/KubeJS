package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerJS;
import dev.architectury.architectury.event.events.EntityEvent;
import net.minecraft.world.InteractionResult;
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
		EntityEvent.LIVING_ATTACK.register(KubeJSEntityEventHandler::livingAttack);
		EntityEvent.ADD.register(KubeJSEntityEventHandler::entitySpawned);
	}

	private static InteractionResult livingDeath(LivingEntity entity, DamageSource source) {
		if (entity != null && new LivingEntityDeathEventJS(entity, source).post(KubeJSEvents.ENTITY_DEATH)) {
			return InteractionResult.FAIL;
		}
		return InteractionResult.PASS;
	}

	private static InteractionResult livingAttack(LivingEntity entity, DamageSource source, float amount) {
		if (entity != null && amount > 0F && new LivingEntityAttackEventJS(entity, source, amount).post(KubeJSEvents.ENTITY_ATTACK)) {
			return InteractionResult.FAIL;
		}
		return InteractionResult.PASS;
	}

	private static InteractionResult entitySpawned(Entity entity, Level world) {
		if (entity != null && ServerJS.instance != null && ServerJS.instance.overworld != null && !world.isClientSide() && new EntitySpawnedEventJS(entity, world).post(ScriptType.SERVER, KubeJSEvents.ENTITY_SPAWNED)) {
			return InteractionResult.FAIL;
		}
		return InteractionResult.PASS;
	}
}
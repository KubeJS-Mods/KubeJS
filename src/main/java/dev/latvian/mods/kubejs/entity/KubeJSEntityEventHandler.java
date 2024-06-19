package dev.latvian.mods.kubejs.entity;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.bindings.event.EntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;

@EventBusSubscriber(modid = KubeJS.MOD_ID)
public class KubeJSEntityEventHandler {
	@SubscribeEvent
	public static void checkSpawn(FinalizeSpawnEvent event) {
		var key = event.getEntity().getType().kjs$getRegistryKey();

		if (event.getLevel() instanceof ServerLevel level && EntityEvents.CHECK_SPAWN.hasListeners(key)) {
			var result = EntityEvents.CHECK_SPAWN.post(level, key, new CheckLivingEntitySpawnKubeEvent(
				event.getEntity(),
				level,
				event.getX(),
				event.getY(),
				event.getZ(),
				event.getSpawnType(),
				event.getSpawner()
			));

			if (result.interruptFalse() || result.interruptTrue()) {
				event.setSpawnCancelled(result.interruptFalse());
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void livingDeath(LivingDeathEvent event) {
		var key = event.getEntity().getType().kjs$getRegistryKey();

		if (EntityEvents.DEATH.hasListeners(key)) {
			EntityEvents.DEATH.post(event.getEntity(), key, new LivingEntityDeathKubeEvent(event.getEntity(), event.getSource())).applyCancel(event);
		}
	}

	@SubscribeEvent
	public static void livingHurt(LivingHurtEvent event) {
		var key = event.getEntity().getType().kjs$getRegistryKey();

		if (EntityEvents.HURT.hasListeners(key)) {
			EntityEvents.HURT.post(event.getEntity(), key, new LivingEntityHurtKubeEvent(event.getEntity(), event.getSource(), event.getAmount())).applyCancel(event);
		}
	}

	@SubscribeEvent
	public static void entitySpawned(EntityJoinLevelEvent event) {
		var key = event.getEntity().getType().kjs$getRegistryKey();

		if (EntityEvents.SPAWNED.hasListeners(key) && event.getLevel() instanceof ServerLevel level) {
			EntityEvents.SPAWNED.post(level, key, new EntitySpawnedKubeEvent(event.getEntity(), level)).applyCancel(event);
		}
	}

	@SubscribeEvent
	public static void livingDrops(LivingDropsEvent event) {
		var key = event.getEntity().getType().kjs$getRegistryKey();

		if (EntityEvents.ENTITY_DROPS.hasListeners(key)) {
			var e = new LivingEntityDropsKubeEvent(event);

			if (!EntityEvents.ENTITY_DROPS.post(event.getEntity(), key, e).applyCancel(event) && e.eventDrops != null) {
				event.getDrops().clear();
				event.getDrops().addAll(e.eventDrops);
			}
		}
	}
}